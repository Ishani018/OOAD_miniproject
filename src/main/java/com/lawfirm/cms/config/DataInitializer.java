package com.lawfirm.cms.config;

import com.lawfirm.cms.model.*;
import com.lawfirm.cms.repository.UserRepository;
import com.lawfirm.cms.service.NotificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // Create Admin
        Admin admin = new Admin("System Admin", "admin@lawfirm.com",
                passwordEncoder.encode("admin123"));
        admin = (Admin) userRepository.save(admin);

        // Create Lawyer
        Lawyer lawyer = new Lawyer("James Mitchell", "lawyer@lawfirm.com",
                passwordEncoder.encode("lawyer123"), "Criminal Law");
        lawyer = (Lawyer) userRepository.save(lawyer);

        // Create Client
        Client client = new Client("Sarah Johnson", "client@lawfirm.com",
                passwordEncoder.encode("client123"), "+1-555-0101");
        client = (Client) userRepository.save(client);

        // Create Staff
        Staff staff = new Staff("Emily Davis", "staff@lawfirm.com",
                passwordEncoder.encode("staff123"));
        staff = (Staff) userRepository.save(staff);

        // Subscribe all users to notifications
        notificationService.subscribe(admin.getId());
        notificationService.subscribe(lawyer.getId());
        notificationService.subscribe(client.getId());
        notificationService.subscribe(staff.getId());

        System.out.println("=== Default users created ===");
        System.out.println("Admin:  admin@lawfirm.com / admin123");
        System.out.println("Lawyer: lawyer@lawfirm.com / lawyer123");
        System.out.println("Client: client@lawfirm.com / client123");
        System.out.println("Staff:  staff@lawfirm.com / staff123");
    }
}
