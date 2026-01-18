package org.example.stolikonline1.models.entities;

import jakarta.persistence.*;
import org.example.stolikonline1.models.enums.UserRoles;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private UserRoles name;

    public Role() {}

    public Role(UserRoles name) {
        this.name = name;
    }

    public UserRoles getName() {
        return name;
    }

    public void setName(UserRoles name) {
        this.name = name;
    }
}
