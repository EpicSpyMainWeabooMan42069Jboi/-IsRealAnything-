package com.epicspymain.isrealanything.client.lang;

public class LangTostMessages$Lines

import net.minecraft.class_2561;

public final class Lines extends Record {
    private final String title;

    private final String body;

    public Lines(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public final String toString() {
        // Byte code:
        //   0: aload_0
        //   1: <illegal opcode> toString : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;)Ljava/lang/String;
        //   6: areturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #18	-> 0
        // Local variable table:
        //   start	length	slot	name	descriptor
        //   0	7	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
    }

    public final int hashCode() {
        // Byte code:
        //   0: aload_0
        //   1: <illegal opcode> hashCode : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;)I
        //   6: ireturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #18	-> 0
        // Local variable table:
        //   start	length	slot	name	descriptor
        //   0	7	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
    }

    public final boolean equals(Object o) {
        // Byte code:
        //   0: aload_0
        //   1: aload_1
        //   2: <illegal opcode> equals : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;Ljava/lang/Object;)Z
        //   7: ireturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #18	-> 0
        // Local variable table:
        //   start	length	slot	name	descriptor
        //   0	8	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
        //   0	8	1	o	Ljava/lang/Object;
    }

    public String title() {
        return this.title;
    }

    public String body() {
        return this.body;
    }

    class_2561 titleText() {
        return (class_2561)class_2561.method_43470(this.title);
    }

    class_2561 bodyText(String prettyLang) {
        return (class_2561)class_2561.method_43470(this.body.replace("{language}", prettyLang));
    }
}
