package nz.govt.natlib.ndha.wctdpsdepositor;


public class CustomDepositField {

    String formFieldLabel;
    String fieldReference;
    String dcFieldLabel;

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
}
