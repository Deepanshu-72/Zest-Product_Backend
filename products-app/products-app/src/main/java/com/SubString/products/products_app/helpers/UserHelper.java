package com.SubString.products.products_app.helpers;

import java.util.UUID;

public class UserHelper {
    public static UUID parseUUID(String uuid){
        return UUID.fromString(uuid);
    }
}
