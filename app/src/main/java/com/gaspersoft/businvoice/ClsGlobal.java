package com.gaspersoft.businvoice;

public class ClsGlobal {
    public static String getBaseUrl() {
        return "https://viaunooapi.gaspersoft.com";
    }

    public static boolean isRUCValid(long ruc){
        return isRUCValid(String.valueOf(ruc));
    }

    public static boolean isRUCValid(String ruc){
        if (ruc == null) {
            return false;
        }

        final int[] multipliers = {5,4,3,2,7,6,5,4,3,2};
        final String[] prefixes = getRucPrefixes();
        final int length = multipliers.length + 1;

        if(ruc.length() != length){
            return false;
        }

        boolean isPrefixOk = false;

        for (String prefix : prefixes){
            if(ruc.substring(0,2).equals(prefix)){
                isPrefixOk = true;
                break;
            }
        }

        if(!isPrefixOk){
            return false;
        }

        int sum = 0;

        for(int i = 0; i < multipliers.length; i++){
            final char section = ruc.charAt(i);

            if(!Character.isDigit(section)){
                return false;
            }

            sum += Character.getNumericValue(ruc.charAt(i)) * multipliers[i];
        }

        final int rest = sum % length;
        final String response = String.valueOf(length - rest);

        return response.charAt(response.length() - 1) == ruc.charAt(ruc.length() - 1);
    }

    public static String[] getRucPrefixes(){
        return new String[]{"10", "15", "17", "20"};
    }
}
