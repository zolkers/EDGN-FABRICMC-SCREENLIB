package com.edgn.ui.template;

public class TemplateSettings {
    private boolean header = true;
    private boolean footer = true;

    public TemplateSettings() {/*Empty for chaining settings*/}

    public TemplateSettings setHeader(boolean headerState) {
        this.header = headerState;
        return this;
    }

    public TemplateSettings setFooter(boolean footerState) {
        this.footer = footerState;
        return this;
    }

    public boolean hasHeader() {
        return this.header;
    }

    public boolean hasFooter(){
        return this.footer;
    }
}