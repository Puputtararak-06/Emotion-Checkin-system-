package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hr")
public class HR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String department;
    private String role; // HR

    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin; 
}