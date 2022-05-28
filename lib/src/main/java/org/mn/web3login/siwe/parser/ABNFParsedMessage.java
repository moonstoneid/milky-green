package org.mn.web3login.siwe.parser;

import lombok.Getter;
import org.mn.web3login.siwe.error.ErrorTypes;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.siwe.parser.apg.Ast;
import org.mn.web3login.siwe.parser.apg.Parser;
import org.mn.web3login.siwe.parser.apg.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ABNFParsedMessage {

    /**
     * RFC 4501 dns authority that is requesting the signing.
     */
    private String mDomain;

    /**
     * Ethereum address performing the signing conformant to capitalization
     * encoded checksum specified in EIP-55 where applicable.
     */
    private String mAddress;

    /**
     * Optional human-readable ASCII assertion that the user will sign, and it must not
     * contain `\n`.
     */
    private String mStatement;

    /**
     * RFC 3986 URI referring to the resource that is the subject of the signing
     * (as in the __subject__ of a claim).
     */
    private String mUri;

    /**
     * Current version of the message.
     */
    private String mVersion;

    /**
     * EIP-155 Chain ID to which the session is bound, and the network where
     * Contract Accounts must be resolved.
     */
    private int mChainId;

    /**
     * Randomized token used to prevent replay attacks, at least 8 alphanumeric
     * characters.
     */
    private String mNonce;

    /**
     * ISO 8601 datetime string of the current time.
     */
    private String mIssuedAt;

    /**
     * Optional ISO 8601 datetime string that, if present, indicates when the signed
     * authentication message is no longer valid.
     */
    private String mExpirationTime;

    /**
     * Optional ISO 8601 datetime string that, if present, indicates when the signed
     * authentication message will become valid.
     */
    private String mNotBefore;

    /**
     * Optional system-specific identifier that may be used to uniquely refer to the
     * sign-in request.
     */
    private String mRequestId;

    /**
     * List of information or references to information the user wishes to have
     * resolved as part of authentication by the relying party. They are
     * expressed as RFC 3986 URIs separated by `\n- `.
     */
    private String[] mResources;

    public ABNFParsedMessage(String msg) throws SiweException {
        parse(msg);
    }

    /**
     * Tries to parse the given string. The given string must be an EIP-4361 formatted message, otherwise an
     * exception is thrown.
     * <p>
     * * @param msg
     */
    private void parse(String msg) throws SiweException {
        Parser parser = new Parser(SiweGrammar.getInstance());
        parser.setStartRule(SiweGrammar.RuleNames.SIGN_IN_WITH_ETHEREUM.ruleID());
        parser.setInputString(msg);

        Ast ast = parser.enableAst(true);

        try {
            ast.enableRuleNode(SiweGrammar.RuleNames.SIGN_IN_WITH_ETHEREUM.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.DOMAIN.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.ADDRESS.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.STATEMENT.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.URI.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.VERSION.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.NONCE.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.ISSUED_AT.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.EXPIRATION_TIME.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.NOT_BEFORE.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.REQUEST_ID.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.CHAIN_ID.ruleID(), true);
            ast.enableRuleNode(SiweGrammar.RuleNames.RESOURCES.ruleID(), true);
            Parser.Result parse = parser.parse();

            if (!parse.success()) {
                throw new SiweException("ABNF parsing failed", ErrorTypes.MALFORMED_MESSAGE);
            }

            ast.setRuleCallback(SiweGrammar.RuleNames.DOMAIN.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.DOMAIN.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.ADDRESS.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.ADDRESS.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.STATEMENT.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.STATEMENT.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.URI.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.URI.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.VERSION.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.VERSION.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.NONCE.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.NONCE.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.ISSUED_AT.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.ISSUED_AT.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.EXPIRATION_TIME.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.EXPIRATION_TIME.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.NOT_BEFORE.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.NOT_BEFORE.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.REQUEST_ID.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.REQUEST_ID.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.CHAIN_ID.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.CHAIN_ID.ruleEnumName()));
            ast.setRuleCallback(SiweGrammar.RuleNames.RESOURCES.ruleID(), new AstTranslator(ast,
                    SiweGrammar.RuleNames.RESOURCES.ruleEnumName()));
            ast.translateAst();

        } catch (Exception e) {
            throw new SiweException("Id out of range. Parsing failed", ErrorTypes.MALFORMED_MESSAGE);
        }
    }

    class AstTranslator extends Ast.AstCallback {
        private final String mNodeName;

        AstTranslator(Ast ast, String name) {
            super(ast);
            mNodeName = name;
        }

        @Override
        public boolean preBranch(int offset, int length) {
            String input = new String(callbackData.inputString);
            String substring = input.substring(offset, offset + length);
            int maxLength = substring.length();

            String value = Utilities.charArrayToString(callbackData.inputString, offset, length, maxLength);
            switch (SiweGrammar.RuleNames.valueOf(mNodeName)) {
                case DOMAIN:
                    mDomain = value;
                    break;
                case ADDRESS:
                    mAddress = value;
                    break;
                case STATEMENT:
                    mStatement = value;
                    break;
                case URI:
                    mUri = value;
                    break;
                case VERSION:
                    mVersion = value;
                    break;
                case NONCE:
                    mNonce = value;
                    break;
                case ISSUED_AT:
                    mIssuedAt = value;
                    break;
                case EXPIRATION_TIME:
                    mExpirationTime = value;
                    break;
                case NOT_BEFORE:
                    mNotBefore = value;
                    break;
                case REQUEST_ID:
                    mRequestId = value;
                    break;
                case CHAIN_ID:
                    mChainId = Integer.parseInt(value);
                    break;
                case RESOURCES:
                    // Split resources by \n
                    String[] tmp =
                            Arrays.stream(substring.split("\n")).filter(x -> !x.isEmpty()).toArray(String[]::new);
                    // Remove "- " at the beginning of each resource
                    mResources = Arrays.stream(tmp).map(s -> s.replace("- ", "")).collect(Collectors.toList()).toArray(new String[0]);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void postBranch(int offset, int length) {
        }
    }

}

