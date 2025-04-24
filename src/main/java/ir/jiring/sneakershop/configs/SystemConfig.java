package ir.jiring.sneakershop.configs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_registration_password", nullable = false)
    private String adminRegistrationPassword;

    public SystemConfig(String adminRegistrationPassword) {
        this.adminRegistrationPassword = adminRegistrationPassword;
    }
}