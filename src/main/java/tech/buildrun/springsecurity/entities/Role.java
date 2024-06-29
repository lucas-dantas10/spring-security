package tech.buildrun.springsecurity.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "s_roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    private String name;

    public Long getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Values {
        ADMIN(1L, "admin"),
        BASIC(2L, "basic");

        long roleId;
        String name;

        Values(long roleId, String name) {
            this.roleId = roleId;
            this.name = name;
        }

        public long getRoleId() {
            return roleId;
        }

        public String getName() {
            return name;
        }
    }
}
