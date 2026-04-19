package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.Document;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.service.CaseService;
import com.lawfirm.cms.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class DocumentController {

    private final DocumentService documentService;
    private final CaseService caseService;

    public DocumentController(DocumentService documentService, CaseService caseService) {
        this.documentService = documentService;
        this.caseService = caseService;
    }

    @GetMapping("/cases/{caseId}/documents/upload")
    public String uploadForm(@PathVariable Long caseId, Model model) {
        Optional<LegalCase> optCase = caseService.findById(caseId);
        if (optCase.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("legalCase", optCase.get());
        model.addAttribute("caseId", caseId);
        return "documents/upload";
    }

    @PostMapping("/cases/{caseId}/documents/upload")
    public String uploadDocument(@PathVariable Long caseId,
                                 @RequestParam String documentName,
                                 @RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<LegalCase> optCase = caseService.findById(caseId);
            if (optCase.isPresent()) {
                Document doc = documentService.uploadDocument(documentName, file, optCase.get());
                if (doc.isScanPassed()) {
                    redirectAttributes.addFlashAttribute("success",
                            "Document '" + doc.getFileName() + "' uploaded successfully — virus scan passed ✓");
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "Document '" + doc.getFileName() + "' uploaded but FAILED virus scan ✗ — file may be unsafe.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Case not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload document: " + e.getMessage());
        }
        return "redirect:/cases/" + caseId;
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        try {
            Optional<Document> optDoc = documentService.findById(id);
            if (optDoc.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Document doc = optDoc.get();
            if (doc.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(doc.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/documents/{id}/delete")
    public String deleteDocument(@PathVariable Long id,
                                 @RequestParam(required = false) Long caseId,
                                 RedirectAttributes redirectAttributes) {
        try {
            documentService.deleteDocument(id);
            redirectAttributes.addFlashAttribute("success", "Document deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete document: " + e.getMessage());
        }
        if (caseId != null) {
            return "redirect:/cases/" + caseId;
        }
        return "redirect:/cases";
    }
}
