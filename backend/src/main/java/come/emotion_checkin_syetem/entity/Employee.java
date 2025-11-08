package come.emotion_checkin_syetem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String department;
    private String position;
    private String role; // EMPLOYEE

    @ManyToOne
    @JoinColumn(name = "hr_id")
    private HR hr;
}