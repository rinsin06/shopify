package com.shopify.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import org.springframework.web.multipart.MultipartFile;



import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    @NonNull
    
    private String email;

    
    
    private String phoneNumber;

    private String password;

    private String profileImageName;

//    private boolean active;
    @Getter
    private String otp;

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Column(name = "otp_generated_time")
    private LocalDateTime otpGeneratedTime;
 
    @Getter
    private boolean otpVerified;

    public void setOtpVerified(boolean otpVerified) {
        this.otpVerified = otpVerified;
    }

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active;

    @Getter
    private boolean blocked;//for in admin panel check whether user is blocked or not.

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CouponUsage> couponUsages = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private CouponUsage appliedCouponUsage;
    

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalletTransaction> walletTransactions = new ArrayList<>();


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "wallet_id")
//    private Wallet wallet;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

    public boolean hasUsedCoupon(Coupon coupon) {
        return couponUsages.stream()
                .anyMatch(couponUsage -> couponUsage.getCoupon().equals(coupon));
    }

    public Coupon getAppliedCoupon() {
        if (appliedCouponUsage != null) {
            return appliedCouponUsage.getCoupon();
        }
        return null;
    }

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Order> orders = new ArrayList<>();

    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }

    public User(){

    }

	
}
