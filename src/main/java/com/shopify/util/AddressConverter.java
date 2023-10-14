package com.shopify.util;

import com.shopify.model.Address;

public class AddressConverter {
    public static Address convertStringToAddress(String addressString) {
        Address address = new Address();

        return address;
    }

    public static String convertAddressToString(Address address) {
        // Create a formatted string that represents the address
        String formattedAddress = String.format(
                "Full Name: %s, Mobile: %s, Pin: %s, Building: %s, Street: %s, Landmark: %s, City: %s, State: %s, Country: %s",
                address.getFullName(),
                address.getMobilePhoneNumber(),
                address.getPinCode(),
                address.getBuildingNo(),
                address.getStreet(),
                address.getLandmark(),
                address.getCity(),
                address.getState(),
                address.getCountry().getName()
        );

        return formattedAddress;
    }
}
