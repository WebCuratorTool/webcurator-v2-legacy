package nz.govt.natlib.ndha.wctdpsdepositor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer on 19/05/2016.
 */
public class CustomDepositFieldMapping {

    private Map<String, Map<String, String>> customDepositFormFieldMaps = new HashMap<String, Map<String, String>>();

//    public CustomDepositMapping(Map<String, Map<String, String>> customDepositFormFieldMaps) {
//        this.customDepositFormFieldMaps = customDepositFormFieldMaps;
//    }

    public void setCustomDepositFormFieldMaps(Map<String, Map<String, String>> customDepositFormFieldMaps) {
        this.customDepositFormFieldMaps = customDepositFormFieldMaps;
    }

    public boolean hasFormMapping(String key){
        if(customDepositFormFieldMaps.containsKey(key)){
            return true;
        }
        else return false;
    }
    public Map<String, String> getFormMapping(String key){
        return customDepositFormFieldMaps.get(key);
    }
}
