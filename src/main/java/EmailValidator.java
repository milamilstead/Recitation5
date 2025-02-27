public class EmailValidator {
    public static final String PRINTABLE_CHAR = "!#$%&'*+-/=?^_`{|}~";
    public static final char DOT = '.';
    public static final char HYPHEN = '-';
    public static final char AT = '@';

    private enum Local_Part{
        LOCAL_START,
        LOCAL_PART,
        ONE_DOT ,
        INVALID
    }
    private enum Domain_{
        DOMAIN_START,
        DOMAIN_LABEL,
        DOMAIN_HYPHEN,
        DOMAIN_DOT,
        INVALID
    }
    public static boolean isEmailValid(String email){
        int atLocation = email.indexOf(AT);
        if(atLocation < 1 || atLocation != email.lastIndexOf(AT)){
            return false;
        }
        String localPart = email.substring(0,atLocation);
        String domain = email.substring(atLocation + 1);

        return valLocal(localPart) && valDomain(domain);
    }

    private static boolean valLocal(String localPart) {
        if (localPart.isEmpty() || localPart.length() > 64) {
            return false;
        }

        Local_Part state = Local_Part.LOCAL_START;

        for (int i = 0; i < localPart.length(); i++) {
            char ch = localPart.charAt(i);

            switch (state) {
                case LOCAL_START:
                    if (ch == DOT || isLocalPartChar(ch)) {
                        return false;
                    }
                    state = Local_Part.LOCAL_PART;
                    break;

                case LOCAL_PART:
                    if (ch == DOT) {
                        state = Local_Part.ONE_DOT;
                    } else if (isLocalPartChar(ch)) {
                        return false;
                    }
                    break;

                case ONE_DOT:
                    if (ch == DOT || isLocalPartChar(ch)) {
                        return false;
                    }
                    state = Local_Part.LOCAL_PART;
                    break;

            }
        }

        return state != Local_Part.ONE_DOT;    }

    private static boolean valDomain(String domain) {
        if (domain.isEmpty() || domain.length() > 253) {
            return false;
        }

        Domain_ state = Domain_.DOMAIN_START;
        int labelLength = 0;
        int totalDomainCount = 0;

        for (int i = 0; i < domain.length(); i++) {
            char ch = domain.charAt(i);
            totalDomainCount++;

            if (totalDomainCount > 253) {
                return false;
            }

            switch (state) {
                case DOMAIN_START:
                    if (ch == HYPHEN || ch == DOT) {
                        return false;
                    } else if (isLetterOrDigit(ch)) {
                        state = Domain_.DOMAIN_LABEL;
                        labelLength = 1;
                    } else {
                        return false;
                    }
                    break;

                case DOMAIN_LABEL:
                    if (ch == DOT) {
                        if (labelLength == 0 || labelLength > 63) {
                            return false;
                        }
                        state = Domain_.DOMAIN_DOT;
                        labelLength = 0;
                    } else if (ch == HYPHEN) {
                        if (labelLength == 0) {
                            return false;
                        }
                        state = Domain_.DOMAIN_HYPHEN;
                        labelLength++;
                    } else if (isLetterOrDigit(ch)) {
                        labelLength++;
                        if (labelLength > 63) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;

                case DOMAIN_HYPHEN:
                    if (ch == DOT) {
                        return false;
                    } else if (isLetterOrDigit(ch)) {
                        state = Domain_.DOMAIN_LABEL;
                        labelLength++;
                        if (labelLength > 63) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;

                case DOMAIN_DOT:
                    if (ch == DOT || ch == HYPHEN) {
                        return false;
                    } else if (isLetterOrDigit(ch)) {
                        state = Domain_.DOMAIN_LABEL;
                        labelLength = 1;
                    } else {
                        return false;
                    }
                    break;

            }
        }

        return state != Domain_.DOMAIN_DOT && state != Domain_.DOMAIN_HYPHEN && labelLength > 0 && labelLength <= 63;
    }

    private static boolean isLocalPartChar(char ch) {
        if (isLetterOrDigit(ch)) return false;
        return PRINTABLE_CHAR.indexOf(ch) < 0;
    }

    private static boolean isLetterOrDigit(char ch) {
        return Character.isLetterOrDigit(ch);
    }

}

