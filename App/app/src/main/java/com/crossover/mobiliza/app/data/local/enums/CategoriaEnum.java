package com.crossover.mobiliza.app.data.local.enums;

public enum CategoriaEnum {

    ANIMAIS("Animais"),
    EDUCACAO("Educação"),
    ESPORTE("Esporte"),
    HUMANITARIO("Humanitário"),
    MEIO_AMBIENTE("Meio Ambiente"),
    TURISMO("Turismo");


    private String text;

    CategoriaEnum(String c) {
        this.text = c;
    }

    public static CategoriaEnum fromText(String text) throws Exception {
        for (CategoriaEnum value : values()) {
            if (value.text.equals(text))
                return value;
        }
        throw new Exception();
    }

    public String getText() {
        return this.text;
    }

    public static String[] getAsArray(){
        String[] array = new String[6];
        array[0] = ANIMAIS.getText();
        array[1] = EDUCACAO.getText();
        array[2] = ESPORTE.getText();
        array[3] = HUMANITARIO.getText();
        array[4] = MEIO_AMBIENTE.getText();
        array[5] = TURISMO.getText();

        return array;
    }
}
