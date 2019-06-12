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
}
