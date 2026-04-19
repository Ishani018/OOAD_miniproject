package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Document;
import com.lawfirm.cms.model.DocumentScanStatus;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final NotificationService notificationService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository,
                           NotificationService notificationService) {
        this.documentRepository = documentRepository;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public List<Document> findByCase(LegalCase legalCase) {
        return documentRepository.findByLegalCase(legalCase);
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * Upload an actual file — saves it to disk and runs a simulated virus scan.
     */
    public Document uploadDocument(String name, MultipartFile file, LegalCase legalCase) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        // Generate a unique filename to avoid collisions
        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetPath = Paths.get(uploadDir).resolve(storedFilename);

        // Save the file to disk
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Build the Document entity
        Document document = new Document();
        document.setName(name);
        document.setFileName(originalFilename);
        document.setFilePath(targetPath.toString());
        document.setFileSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setLegalCase(legalCase);
        document.setUploadDate(LocalDateTime.now());
        document.setVersion(1);
        document.setScanStatus(DocumentScanStatus.PENDING);

        // Run virus scan simulation
        document.setScanStatus(DocumentScanStatus.SCANNING);
        boolean scanResult = scanDocument(targetPath);
        document.setScanned(true);
        document.setScanPassed(scanResult);
        document.setScanStatus(scanResult ? DocumentScanStatus.CLEAN : DocumentScanStatus.INFECTED);

        Document saved = documentRepository.save(document);

        if (scanResult) {
            notificationService.notifyObservers("Document uploaded and scan passed: " + name, legalCase.getId());
        } else {
            notificationService.notifyObservers("Document uploaded but FAILED virus scan: " + name, legalCase.getId());
        }

        return saved;
    }

    /**
     * Simulated virus scan. Reads the first bytes of the file and checks for
     * suspicious patterns (EICAR test string, executable headers, etc.).
     * Returns true if the file is clean, false if it looks suspicious.
     */
    private boolean scanDocument(Path filePath) {
        try {
            byte[] header = new byte[Math.min(1024, (int) Files.size(filePath))];
            try (InputStream is = Files.newInputStream(filePath)) {
                is.read(header);
            }

            String headerStr = new String(header);

            // Check for EICAR test string (antivirus test pattern)
            if (headerStr.contains("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR")) {
                return false;
            }

            // Check for executable signatures
            if (header.length >= 2) {
                // MZ header (Windows executables)
                if (header[0] == 0x4D && header[1] == 0x5A) {
                    return false;
                }
                // ELF header (Linux executables)
                if (header.length >= 4 && header[0] == 0x7F && header[1] == 0x45
                        && header[2] == 0x4C && header[3] == 0x46) {
                    return false;
                }
            }

            // Check for suspicious script content
            String lowerHeader = headerStr.toLowerCase();
            if (lowerHeader.contains("<script>") && lowerHeader.contains("document.cookie")) {
                return false;
            }

            // File passes scan
            return true;
        } catch (IOException e) {
            // If we can't read the file, fail the scan for safety
            return false;
        }
    }

    public void deleteDocument(Long id) {
        Optional<Document> optDoc = documentRepository.findById(id);
        if (optDoc.isPresent()) {
            Document doc = optDoc.get();
            // Delete the physical file if it exists
            if (doc.getFilePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(doc.getFilePath()));
                } catch (IOException e) {
                    // Log but don't fail — the DB record will still be removed
                }
            }
        }
        documentRepository.deleteById(id);
    }
}
