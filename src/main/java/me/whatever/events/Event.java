package me.whatever.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.whatever.accounts.Account;
import me.whatever.accounts.AccountSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING) // 기본값은 순서로 ordinal, but String은 String 그대로 사용가능
    private EventStatus eventStatus = EventStatus.DRAFT;
    // Event에서 단방향으로 참조
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    public void update() {
        // Update free
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }
        // Update offline
        if (this.location == null || this.location.isBlank()) { // trim 후 비어있는지 확인해줌
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
