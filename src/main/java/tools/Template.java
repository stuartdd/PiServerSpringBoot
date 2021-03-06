/*
 * Copyright (C) 2018 stuartdd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Template.java
 *
 * Created on 20 August 2007, 15:26
 *
 */
package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class manages Templates of any text file type.<br/>
 * It reads the template fragments via a URL which makes it easier to manage
 * HTML and web resources. <br/>
 * All substitution parameters are provided via a <b>java.util.Map</b> object.
 * The Map object is searched<br/>
 * for the parameter. The object returned us substituted via the getString()
 * method.<br/>
 * <br/>
 * <h2>A complete example: is in the test library:ExampleRepeat.java</h2>
 * </code> <h2>Template control tags:</h2> <h3>The following tags result in
 * additional text from other templates being inserted in to the output</h3>
 * <b>template#</b> - eg: %{template#file_006.html} Include template
 * file_006.html at this point.<br/>
 * <b>template?</b> - eg: %{template?varName} Include the template named in
 * property varName at this point.<br/>
 * <b>repeat#</b> - eg: %{repeat#file_006.html} Repeatedly include template
 * file_006.html at this point. See above example.<br/>
 * <h3>The following tags will not add text to the output. They manipulate the
 * existing data set.</h3> <b>set#</b> - eg: %{set#abc=123} Create value abc and
 * set it's value to 123. For definition inline<br/>
 * <b>set?</b> - eg: %{set?abc=123} Create value named by the value of abc and
 * set it's value to 123. For definition inline<br/>
 * <b>unSet#</b> - eg: %{set?abc=123} remove the value abc. For removal
 * inline<br/>
 * <b>bundle#</b> - eg: %{bundle#file_006.properties} This includes the set of
 * name value pairs in the properties file.<br/>
 * <b>bundle?</b> - eg: %{bundle?varName} This includes the set of name value
 * pairs in the properties file defined in varName.<br/>
 * <b>Notes on using bundles</b><br/>
 * - Bundles are within the scope of a template. A bundle loaded in the top
 * templete will provide values in ALL included (sub) templates.<br/>
 * A bundle included in a sub template overrides the higher level bundle and
 * will provide values in ALL included (sub/sub) templates. ETC.<br/>
 * - Do not load bundles in <b>repeat#</b> templates as they will be loaded each
 * time.<br/>
 * - Only ONE bundle per template can be loaded. A second bundle will replace
 * the first.<br/>
 * - Bundles are useful for String constants that can be defined once outside
 * the template.<br/>
 * <h3>The following tags are flow control tags</h3> <b>ifDef#</b> - eg:
 * %{ifDef#abc} The text following this tag, up to %{fi#} is included if abc is
 * defined<br/>
 * <b>ifUnDef#</b> - eg: %{ifUnDef#abc} The text following this tag, up to
 * %{fi#} is included if abc is NOT defined<br/>
 * <b>fi#</b> - eg: %{fi#} closes the ifDef and idUnDef tags. Dont forget the #
 * on this control tag<br/>
 * <br/>
 * <b>NOTE THAT ifDef/fi and ifUnDef/fi can be nested to any (reasonable)
 * depth</b><br/>
 * <p/>
 * <
 * pre>
 * <code>
 * 1 <b>%{ifDef#abc}</b>abc is defined and contains (<b>%{abc}</b>) so this is
 * included.<b>%{fi#}</b>
 * 2 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{set#xyz=123}</b>3 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{unSet#xyz}</b>4 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * <b>%{unSet#abc}</b>5 <b>%{ifDef#abc}</b>abc <b>%{ifDef#xyz}</b>and xyz are
 * both<b>%{fi#}</b><b>%{ifUnDef#xyz}</b>is<b>%{fi#}</b> defined so this is
 * included.<b>%{fi#}</b>
 * </pre>
 * <p/>
 * </code> If abc is defined in a data map before tailoring begins then this
 * will be the outcome:<br/>
 * <p/>
 * <
 * pre>
 * <code>
 * 1 abc is defined and contains (--abc--) so this is included.
 * 2 abc is defined so this is included.
 * 3 abc and xyz are both defined so this is included.
 * 4 abc is defined so this is included.
 * 5
 * </pre>
 * <p/>
 * </code> The following tags is for debugging<br/>
 * <b>listData</b> - eg: %{listData} Will list ALL data in the HashMap passed to
 * the parse method<br/>
 *
 * @author - Stuart Davies Davies (802996013)
 * @version $Rev: $ $Date: $
 */
public class Template {

    public static enum URESOLVED_ACTION {
        BLANK, IGNORE, SHOW
    };
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String RESOURCE_PREFIX = "classpath:";
    private static final String TEMPLATE_STR = "Template";
    private static final String BUNDLE_STR = "Bundle:";
    private static final String IN_STR_MODE = " in String mode";
    private static final String CANNOT_USE = "Cannot use ";
    private static final String NOT_FOUND = ":notFound";
    private static final String SUB_NOT_FOUND = "' SUBSTITUTION VAR NOT FOUND";
    public static final String ERROR_PREFIX = "{{+++ERROR: ";
    public static final String ERROR_SUFFIX = " +++}}";
    public static final String REPEAT_TP = "Repeat template property:";
    private static final String NL = System.getProperty("line.separator");
    private static final char ID_CHAR = '%';
    private static final String ID_STRING = "%";
    private static final String INC = "template#";
    private static final String INC_VAR = "template?";
    private static final String BUNDLE_REF = "bundle#";
    private static final String BUNDLE_VAR = "bundle?";
    private static final String REPEAT = "repeat#";
    private static final String IF_DEF = "ifDef#";
    private static final String IF_UN_DEF = "ifUnDef#";
    private static final String FI = "fi#";
    private static final String SET_VAR = "set#";
    private static final String SET_VAR_VAR = "set?";
    private static final String UN_SET_VAR = "unSet#";
    private static final String LIST_DATA = "listData";
    private byte[] fragmentStr;
    private int maxIndex;
    private final Deque appendToTemplateStack = new LinkedList();
    private boolean appendToTemplate = true;
    private String fileUrl = null;
    private String templateName = null;
    private Template parent;
    private Properties bundle;
    private boolean loadViaUrl = true;
    private boolean cannotUseInclude = false;
    private static final int VAL_LENGTH = 2;

    private Template() {
    }

    public Template(String fileName) throws TemplateException {
        if (fileName == null) {
            throw new InvalidParameterException("Parameter fileName is null");
        }
        if (fileName.startsWith(RESOURCE_PREFIX)) {
            this.fileUrl = "";
            this.templateName = fileName;
        } else {
            File template = new File(fileName);
            if (!template.exists()) {
                throw new InvalidParameterException(TEMPLATE_STR + " ["
                        + template.getAbsolutePath() + "] does not exist");
            }
            if (!template.isFile()) {
                throw new InvalidParameterException(TEMPLATE_STR + " ["
                        + template.getAbsolutePath() + "] is not a file");
            }
            this.fileUrl = template.getParent();
            this.templateName = template.getName();
        }
        this.parent = null;
        this.loadViaUrl = false;
        this.fragmentStr = load(this.templateName);
        this.maxIndex = this.fragmentStr.length - 1;
    }

    /**
     * Creates a new instance of Fragment
     *
     * @param fileUrl The url of the file
     * @param templateName The name of the template
     * @throws TemplateException
     */
    public Template(String fileUrl, String templateName) throws TemplateException {
        this(fileUrl, true, templateName, null);
    }

    private Template(String fileUrl, boolean loadViaUrl, String templateName, Template parent) throws TemplateException {
        if (templateName == null) {
            throw new TemplateException("Parameter templateName is null");
        }
        this.parent = parent;
        this.templateName = templateName;
        this.fileUrl = fileUrl;
        this.loadViaUrl = loadViaUrl;
        this.fragmentStr = load(this.templateName);
        this.maxIndex = this.fragmentStr.length - 1;
    }

    /**
     * @param s The string to be parsed (containing %{?} tags)
     * @param map The map containing the name value pairs
     * @return The resultant text (without %{?} values)
     */
    public static String parse(String s, Map map) {
        return parse(s, map, false);
    }

    public static String parse(String s, Map map, boolean ignoreUnresolvedSubs) {
        return parse(s, map, (ignoreUnresolvedSubs ? URESOLVED_ACTION.IGNORE : URESOLVED_ACTION.SHOW));
    }

    /**
     * @param s The string to be parsed (containing %{?} tags)
     * @param map The map containing the name value pairs
     * @param ignoreUnresolvedSubs True to leave values that cannot be resolved
     * as they are
     * @return The resultant text (without %{?} values)
     */
    public static String parse(String s, Map map, URESOLVED_ACTION unresolvedSubs) {
        Template t = new Template();
        t.fragmentStr = s.getBytes(UTF_8);
        t.maxIndex = t.fragmentStr.length - 1;
        t.fileUrl = null;
        t.templateName = null;
        t.parent = null;
        t.loadViaUrl = false;
        t.cannotUseInclude = false;
        return t.parse(map, unresolvedSubs);
    }

    private static String[] split(char ch, String s) {
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ch) {
                sb0 = sb;
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        return new String[]{sb0.toString(), sb.toString()};
    }

    public Map getAditionalData() {
        return null;
    }

    public String getTemplateText() {
        return new String(fragmentStr);
    }

    public String parse(Map data1) {
        return parseDual(data1, null, false);
    }

    public String parse(Map data1, URESOLVED_ACTION unresolvedSubs) {
        return parseDual(data1, null, unresolvedSubs);
    }

    public String parse(Map data1, boolean ignoreUnresolvedSubs) {
        return parseDual(data1, null, (ignoreUnresolvedSubs ? URESOLVED_ACTION.IGNORE : URESOLVED_ACTION.SHOW));
    }

    @Override
    public String toString() {
        return TEMPLATE_STR + ":" + templateName;
    }

    private void pushSuspend(boolean newVal) {
        if (appendToTemplate) {
            appendToTemplateStack.push(appendToTemplate);
            appendToTemplate = newVal;
        } else {
            appendToTemplateStack.push(Boolean.FALSE);
        }
    }

    private void popSuspend() {
        if (appendToTemplateStack.isEmpty()) {
            appendToTemplate = true;
        } else {
            appendToTemplate = (boolean) appendToTemplateStack.pop();
        }
    }

    private String parseDual(Map data1, Map data2, boolean ignoreUnresolvedSubs) {
        return parseDual(data1, data2, (ignoreUnresolvedSubs ? URESOLVED_ACTION.IGNORE : URESOLVED_ACTION.SHOW));
    }

    private String parseDual(Map data1, Map data2, URESOLVED_ACTION unresolvedSubs) {
        int pos = 0;
        byte c;
        StringBuilder sbx = new StringBuilder();
        if (pos > maxIndex) {
            c = 0;
        } else {
            c = fragmentStr[pos];
            pos++;
        }
        while (c != 0) {
            if (c == ID_CHAR) {
                if (pos > maxIndex) {
                    if (appendToTemplate) {
                        sbx.append((char) c);
                    }
                } else {
                    c = fragmentStr[pos];
                    pos++;
                    if (c == '{') {
                        String var = parseVarName(pos);
                        if (var == null) {
                            sbx.append(ERROR_PREFIX
                                    + "SUBSTITUTION VAR NOT TERMINATED"
                                    + ERROR_SUFFIX);
                            return sbx.toString();
                        } else {
                            pos = pos + var.length() + 1;
                            String val = lookUpVar(var, data1, data2, unresolvedSubs);
                            if (appendToTemplate) {
                                sbx.append(val);
                            }
                        }
                    } else {
                        if (c == ID_CHAR) {
                            if (appendToTemplate) {
                                sbx.append(ID_CHAR);
                            }
                        } else {
                            if (appendToTemplate) {
                                sbx.append(ID_CHAR);
                            }
                            if (appendToTemplate) {
                                sbx.append((char) c);
                            }
                        }
                    }
                }
            } else {
                if (appendToTemplate) {
                    sbx.append((char) c);
                }
            }
            if (pos > maxIndex) {
                c = 0;
            } else {
                c = fragmentStr[pos];
                pos++;
            }

        }
        return sbx.toString();
    }

    private String lookUpVar(String name, Map data1, Map data2, URESOLVED_ACTION unresolvedSubs) {
        if (name == null || name.length() == 0) {
            return ERROR_PREFIX + "SUBSTITUTION VAR IS EMPTY" + ERROR_SUFFIX;
        }

        if (name.startsWith(BUNDLE_VAR)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + BUNDLE_VAR
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(BUNDLE_VAR.length());
                Object incNameValue = getSubVar(incName, data1, data2);
                if (incNameValue == null) {
                    switch (unresolvedSubs) {
                        case IGNORE:
                            return ID_STRING + '{' + incName + '}';
                        case BLANK:
                            return "";
                        default:
                            return ERROR_PREFIX + "'" + INC_VAR + incName
                                    + SUB_NOT_FOUND + ERROR_SUFFIX;
                    }
                }
                try {
                    bundle = loadBundle(incNameValue.toString());
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + BUNDLE_STR + incNameValue
                            + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(BUNDLE_REF)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + BUNDLE_REF
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String bundleName = name.substring(BUNDLE_REF.length());
                try {
                    bundle = loadBundle(bundleName);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + BUNDLE_STR + bundleName + NOT_FOUND
                            + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(REPEAT)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + REPEAT
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                StringBuilder sb = new StringBuilder();
                String repeatTemplateName = name.substring(REPEAT.length());
                Object o = getSubVar(repeatTemplateName, data1, data2);
                if (o != null) {
                    if (o instanceof String) {
                        repeatTemplateName = (String) o;
                        o = getSubVar(repeatTemplateName, data1, data2);
                    }
                    if (o instanceof List) {
                        List list = (List) o;
                        for (int i = 0; i < list.size(); i++) {
                            Object listMember = list.get(i);
                            if (listMember instanceof Map) {
                                try {
                                    Template f = new Template(fileUrl,
                                            loadViaUrl, repeatTemplateName, this);
                                    sb.append(f.parseDual(data1,
                                            (Map) listMember, unresolvedSubs));
                                } catch (TemplateException ex) {
                                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':'
                                            + repeatTemplateName + NOT_FOUND
                                            + ERROR_SUFFIX, ex);
                                }
                            } else {
                                return ERROR_PREFIX
                                        + REPEAT_TP
                                        + repeatTemplateName
                                        + " must only contain java.util.Map objects"
                                        + ERROR_SUFFIX;
                            }
                        }
                        return sb.toString();
                    } else {
                        return ERROR_PREFIX + REPEAT_TP
                                + repeatTemplateName
                                + " must be of type java.util.List" + ERROR_SUFFIX;
                    }
                } else {
                    return ERROR_PREFIX + REPEAT_TP
                            + repeatTemplateName + NOT_FOUND + ERROR_SUFFIX;
                }
            }
            return "";
        }

        if (name.startsWith(INC)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + INC + IN_STR_MODE
                        + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(INC.length());
                try {
                    Template f = new Template(fileUrl, loadViaUrl, incName,
                            this);
                    return f.parseDual(data1, data2, unresolvedSubs);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':' + incName + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(INC_VAR)) {
            if (cannotUseInclude) {
                return ERROR_PREFIX + CANNOT_USE + INC_VAR
                        + IN_STR_MODE + ERROR_SUFFIX;
            }
            if (appendToTemplate) {
                String incName = name.substring(INC_VAR.length());
                Object incNameValue = getSubVar(incName, data1, data2);
                if (incNameValue == null) {
                    return getUnresolvedValue(incName, unresolvedSubs);
                }
                try {
                    Template f = new Template(fileUrl, loadViaUrl,
                            incNameValue.toString(), this);
                    return f.parseDual(data1, data2, unresolvedSubs);
                } catch (TemplateException ex) {
                    return ignoreException(ERROR_PREFIX + TEMPLATE_STR + ':' + incNameValue + NOT_FOUND + ERROR_SUFFIX, ex);
                }
            }
            return "";
        }

        if (name.startsWith(IF_UN_DEF)) {
            String ifName = name.substring(IF_UN_DEF.length());
            Object ifVal = getSubVar(ifName, data1, data2);
            if (ifVal != null && ifVal.toString().startsWith(ERROR_PREFIX)) {
                ifVal = null;
            }
            pushSuspend(ifVal == null);
            return "";
        }

        if (name.startsWith(IF_DEF)) {
            String ifName = name.substring(IF_DEF.length());
            Object ifVal = getSubVar(ifName, data1, data2);
            if (ifVal != null && ifVal.toString().startsWith(ERROR_PREFIX)) {
                ifVal = null;
            }
            pushSuspend(ifVal != null);
            return "";
        }

        if (name.startsWith(FI)) {
            popSuspend();
            return "";
        }

        if (name.startsWith(SET_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(SET_VAR.length());
                String[] vals = split('=', exp);
                if (vals.length != VAL_LENGTH) {
                    return ERROR_PREFIX + SET_VAR
                            + " expression " + exp + " is invalid" + ERROR_SUFFIX;
                }
                data1.put(vals[0], vals[1]);
            }
            return "";
        }

        if (name.startsWith(SET_VAR_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(SET_VAR_VAR.length());
                String[] vals = split('=', exp);
                if (vals.length != VAL_LENGTH) {
                    return ERROR_PREFIX + SET_VAR_VAR
                            + " expression " + exp + " is invalid" + ERROR_SUFFIX;
                }
                Object var = getSubVar(vals[1], data1, data2);
                if (var == null) {
                    return getUnresolvedValue(vals[1], unresolvedSubs);
                }
                data1.put(vals[0], var);
            }
            return "";
        }

        if (name.startsWith(UN_SET_VAR)) {
            if (appendToTemplate) {
                String exp = name.substring(UN_SET_VAR.length());
                data1.remove(exp);
            }
            return "";
        }

        if (name.equals(LIST_DATA)) {
            return (stringData(data1, data2));
        }

        Object var = getSubVar(name, data1, data2);
        if (var == null) {
            return getUnresolvedValue(name, unresolvedSubs);
        }
        return var.toString();
    }

    private String getUnresolvedValue(String name, URESOLVED_ACTION unresolvedSubs) {
        switch (unresolvedSubs) {
            case IGNORE:
                return ID_STRING + '{' + name + '}';
            case BLANK:
                return "";
            default:
                return ERROR_PREFIX + "'" + INC_VAR + name
                        + SUB_NOT_FOUND + ERROR_SUFFIX;
        }
    }

    protected Object getBundleProperty(String name) {
        if (bundle == null) {
            if (parent == null) {
                return null;
            }
            return parent.getBundleProperty(name);
        }
        Object v = bundle.get(name);
        if (v == null && parent != null) {
            return parent.getBundleProperty(name);
        }
        return v;
    }

    public Object getSubVar(String varName, Map data1, Map data2) {
        Object o = null;
        Map additional = getAditionalData();
        if (additional != null) {
            o = additional.get(varName);
        }
        if (o == null) {
            if (data2 != null) {
                o = data2.get(varName);
            }
            if (o == null) {
                if (data1 != null) {
                    o = data1.get(varName);
                }
                if (o == null) {
                    o = getBundleProperty(varName);
                    if (o == null) {
                        o = System.getProperties().get(varName);
                        if (o == null) {
                            return null;
                        }
                    }
                }
            }
        }
        return o;
    }

    private String parseVarName(int pos) {
        byte c = 0;
        StringBuilder sb = new StringBuilder();
        if (pos > maxIndex) {
            c = 0;
        } else {
            c = fragmentStr[pos];
            pos++;

        }

        while (c != 0) {
            if (c == '}') {
                return sb.toString();
            } else {
                if (c > 31) {
                    sb.append((char) c);
                } else {
                    sb.append('_');
                }

            }
            if (pos > maxIndex) {
                c = 0;
            } else {
                c = fragmentStr[pos];
                pos++;

            }

        }
        return null;
    }

    private String stringData(Map data1, Map data2) {
        StringBuilder sb = new StringBuilder();
        if (data2 != null) {
            sb.append("Data set 2 ---------------- Overrides Data set 1.").append(NL);
            sb.append("Data size = ").append(data2.size()).append(NL);
            for (Iterator it = data2.entrySet().iterator(); it.hasNext();) {
                Object elem = it.next();
                sb.append(elem).append(NL);
            }
        }
        if (data1 != null) {
            sb.append("Data set 1 ----------------").append(NL);
            sb.append("Data size = ").append(data1.size()).append(NL);
            for (Iterator it = data1.entrySet().iterator(); it.hasNext();) {
                Object elem = it.next();
                sb.append(elem).append(NL);
            }
        }
        String ret = sb.toString().trim();
        if (ret.length() == 0) {
            return "Data set is EMPTY";
        }
        return ret;
    }

    private Properties loadBundle(String localBundleName)
            throws TemplateException {
        Properties p = new Properties();
        InputStream fis = null;
        String fileName = null;
        URL url = null;
        try {
            if (loadViaUrl) {
                fileName = fileUrl + "/" + localBundleName;
                url = new URL(fileName);
                p.load(url.openStream());
            } else {
                if (localBundleName.startsWith(RESOURCE_PREFIX)) {
                    fis = this.getClass().getResourceAsStream(localBundleName.substring(RESOURCE_PREFIX.length()));
                    if (fis == null) {
                        throw new TemplateException(ignoreException("Failed to get input stream for [" + fileName + "]"));
                    }
                } else {
                    if ((fileUrl != null) && (fileUrl.trim().length() > 0)) {
                        fileName = fileUrl + File.separator + localBundleName;
                    } else {
                        fileName = localBundleName;
                    }
                    fis = new FileInputStream(fileName);
                }
                p.load(fis);
            }
        } catch (IOException ex) {
            throw new TemplateException(ignoreException("Failed to load bundle at [" + fileName + "] " + ex.getMessage(), ex));
        }
        if (p.isEmpty()) {
            return null;
        }
        return p;
    }

    private byte[] load(String localTemplateName) throws TemplateException {
        InputStream fis = null;
        URL url = null;
        String fileName = null;
        try {
            if (loadViaUrl) {
                fileName = fileUrl + "/" + localTemplateName;
                url = new URL(fileName);
                fis = url.openStream();
            } else {
                if (localTemplateName.startsWith(RESOURCE_PREFIX)) {
                    fis = this.getClass().getResourceAsStream(localTemplateName.substring(RESOURCE_PREFIX.length()));
                    if (fis == null) {
                        throw new TemplateException(ignoreException("Failed to get input stream for [" + fileName + "]"));
                    }
                } else {
                    if ((fileUrl != null) && (fileUrl.trim().length() > 0)) {
                        fileName = fileUrl + File.separator + localTemplateName;
                    } else {
                        fileName = localTemplateName;
                    }
                    fis = new FileInputStream(fileName);
                }
            }
            return loadFromStream(fis, fileName);
        } catch (IOException io) {
            throw new TemplateException(ignoreException("Failed to get input stream for [" + fileName + "] " + io.getMessage(), io));
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException io) {
            }
        }
    }

    protected byte[] loadFromStream(InputStream fis, String fileName) {
        try {
            byte[] targetArray = new byte[fis.available()];
            fis.read(targetArray);
            return targetArray;
        } catch (IOException io) {
            throw new TemplateException(ignoreException("Failed to read input stream for [" + fileName + "] " + io.getMessage(), io));
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException io) {
            }
        }
    }

    protected static final String ignoreException(String message, Exception ex) {
        return ex.getClass().getSimpleName() + ':' + message;
    }

    protected static final String ignoreException(String message) {
        return ':' + message;
    }

}
