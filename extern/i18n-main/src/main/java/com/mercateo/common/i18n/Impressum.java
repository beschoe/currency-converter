package com.mercateo.common.i18n;

import java.util.List;

import com.mercateo.common.i18n.ImpressumBuilder.LineSeparator;

public class Impressum {
    private final List<String> lines;

    Impressum(List<String> lines) {
        this.lines = lines;
    }

    public String getImpressumAsHTML() {
        return getImpressum(LineSeparator.HTML_BR);
    }

    public String getImpressumAsPlain() {
        return getImpressum(LineSeparator.PLAIN_BL_N);
    }

    private String getImpressum(LineSeparator separator) {
        StringBuilder bs = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            bs.append(lines.get(i));
            if (i < (lines.size() - 1)) {
                switch (separator) {
                case HTML_BR:
                    bs.append("<br>");
                    break;
                case PLAIN_BL_N:
                    bs.append("\n");
                    break;
                default:
                    bs.append("\n");
                }
            }
        }
        return bs.toString();
    }

}