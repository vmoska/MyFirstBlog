package com.project.myblog.entity;

import com.project.myblog.entity.audit.DateAudit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="USER")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User extends DateAudit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NaturalId(mutable=true)
    @Column(name="EMAIL", nullable=false, length=120)
    private String email;

    @Column(name="USERNAME", unique=true, nullable=false, length=12)
    private String username;

    @Column(name="PASSWORD", nullable=false, length=100)
    private String password;

    @Column(name="IS_ENABLED", nullable=false, columnDefinition="TINYINT(1)")
    private Boolean enabled = true;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="USER_AUTHORITIES",
            joinColumns = {@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns = {@JoinColumn(name="AUTHORITY_ID", referencedColumnName="ID")})
    private Set<Authority> authorities;

    private Date bannedUntil;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AVATAR")
    private DbFile avatar;

    @Column(name="LOGIN_ATTEMPT", nullable=false, columnDefinition="TINYINT(1)")
    private int loginAttempt = 0;

    public User(String email, String username, String password) {
        super();
        this.email = email;
        this.username = username;
        this.password = password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public User(Long id) {
        super();
        this.id = id;
    }



}

