package com.crossover.mobiliza.app.data.local.enums;

public enum RegiaoEnum {

    CENTRO("Centro"),
    LESTE("Leste"),
    NOROESTE("Noroeste"),
    NORTE("Norte"),
    OESTE("Oeste"),
    SUDOESTE("Sudoeste"),
    SUL("Sul");


    private String text;

    RegiaoEnum(String r) {
        this.text = r;
    }

    public static RegiaoEnum fromText(String text) throws Exception {
        for (RegiaoEnum value : values()) {
            if (value.text.equals(text))
                return value;
        }
        throw new Exception();
    }

    public String getText() {
        return this.text;
    }

    public static String[] getAsArray(){
        String[] array = new String[7];
        array[0] = CENTRO.getText();
        array[1] = LESTE.getText();
        array[2] = NOROESTE.getText();
        array[3] = NORTE.getText();
        array[4] = OESTE.getText();
        array[5] = SUDOESTE.getText();
        array[6] = SUL.getText();

        return array;

    }

}
