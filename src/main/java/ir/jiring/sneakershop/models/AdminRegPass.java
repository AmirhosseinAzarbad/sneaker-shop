package ir.jiring.sneakershop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "system_config")
public class AdminRegPass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "admin_registration_password", nullable = false)
    private String adminRegistrationPassword;

    public AdminRegPass(String adminRegistrationPassword) {
        this.adminRegistrationPassword = adminRegistrationPassword;
    }
}