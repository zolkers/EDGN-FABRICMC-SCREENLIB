package com.edgn.ui.template;

/**
 * This class will set the rules for {@link BaseTemplate}
 * Just like any other class in the framework, the class will work with a chaining logic
 * If the user choses to set the TemplateSettings to null we will simply return the default settings
 * @author EDGN
 */
@SuppressWarnings("unused")
public class TemplateSettings {
    private boolean header = true;
    private boolean footer = true;

    public TemplateSettings() {/*Empty for chaining settings*/}

    public TemplateSettings setToDefault() {
        this.header = true;
        this.footer = true;
        return this;
    }

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