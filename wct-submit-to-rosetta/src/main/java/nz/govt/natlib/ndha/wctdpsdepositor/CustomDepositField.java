package nz.govt.natlib.ndha.wctdpsdepositor;


public class CustomDepositField {

    String formFieldLabel;
    String fieldReference;
    String dcFieldLabel;
    String dcFieldType;
    boolean mandatory;

    public CustomDepositField() {

    }

    public CustomDepositField(String formFieldLabel, String fieldReference, String dcFieldLabel, String dcFieldType){
        this.formFieldLabel = formFieldLabel;
        this.fieldReference = fieldReference;
        this.dcFieldLabel = dcFieldLabel;
        this.dcFieldType = dcFieldType;
        mandatory = false;
    }

    public String getFormFieldLabel() {
        return formFieldLabel;
    }

    public void setFormFieldLabel(String formFieldLabel) {
        this.formFieldLabel = formFieldLabel;
    }

    public String getFieldReference() {
        return fieldReference;
    }

    public void setFieldReference(String fieldReference) {
        this.fieldReference = fieldReference;
    }

    public String getDcFieldLabel() {
        return dcFieldLabel;
    }

    public void setDcFieldLabel(String dcFieldLabel) {
        this.dcFieldLabel = dcFieldLabel;
    }

    public String getDcFieldType() {
        return dcFieldType;
    }

    public void setDcFieldType(String dcFieldType) {
        this.dcFieldType = dcFieldType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
