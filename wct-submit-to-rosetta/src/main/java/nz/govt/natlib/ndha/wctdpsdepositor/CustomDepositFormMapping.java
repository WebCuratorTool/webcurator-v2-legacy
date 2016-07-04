package nz.govt.natlib.ndha.wctdpsdepositor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer on 19/05/2016.
 */
public class CustomDepositFormMapping {

    private Map<String, List<CustomDepositField>> customDepositFormFieldMaps = new HashMap<String, List<CustomDepositField>>();

//    public CustomDepositMapping(Map<String, Map<String, String>> customDepositFormFieldMaps) {
//        this.customDepositFormFieldMaps = customDepositFormFieldMaps;
//    }

    public void setCustomDepositFormFieldMaps(Map<String, List<CustomDepositField>> customDepositFormFieldMaps) {
        this.customDepositFormFieldMaps = customDepositFormFieldMaps;
    }

    public boolean hasFormMapping(String key){
        if(customDepositFormFieldMaps.containsKey(key)){
            return true;
        }
        else return false;
    }
    public List<CustomDepositField> getFormMapping(String key){
        return customDepositFormFieldMaps.get(key);
    }
}
