package io.rocketbase.toggl.backend.model.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contact {

    private String emailAddress;

    private String phone;

    private String street;

    private String postcode;

    private String city;

}
