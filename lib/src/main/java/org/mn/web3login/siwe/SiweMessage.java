package org.mn.web3login.siwe;


import lombok.Getter;
import lombok.experimental.Accessors;
import org.mn.web3login.siwe.error.ErrorTypes;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.siwe.grammar.SiweGrammar;
import org.mn.web3login.siwe.grammar.apg.Ast;
import org.mn.web3login.siwe.grammar.apg.Utilities;
import org.mn.web3login.siwe.util.Utils;
import org.mn.web3login.siwe.util.ValidatorUtils;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates a new SiweMessage<br>
 * <br>
 * A new instance can be created with {@link Builder} or with {@link Parser}.
 */

@Accessors(prefix = "m")
@Getter
public class SiweMessage {

    /**
     * RFC 4501 dns authority that is requesting the signing.
     */
    private String mDomain;

    /**
     * Ethereum address that requested the signing in EIP-55 format.
     */
    private String mAddress;

    /**
     * Optional human-readable ASCII assertion that the user will sign, and it must not contain `\n`.
     */
    private String mStatement;

    /**
     * RFC 3986 URI referring to the resource that is the subject of the signing (as in the __subject__ of a claim).
     */
    private String mUri;

    /**
     * Current version of the message.
     */
    private String mVersion;

    /**
     * EIP-155 Chain ID to which the session is bound, and the network where Contract Accounts must be resolved.
     */
    private int mChainId;

    /**
     * Randomized token used to prevent replay attacks, at least 8 alphanumeric characters.
     */
    private String mNonce;

    /**
     * ISO 8601 datetime string of the current time.
     */
    private String mIssuedAt;

    /**
     * Optional ISO 8601 datetime string that, if present, indicates when the signed authentication message is no longer
     * valid.
     */
    private String mExpirationTime;

    /**
     * Optional ISO 8601 datetime string that, if present, indicates when the signed authentication message will become
     * valid.
     */
    private String mNotBefore;

    /**
     * Optional system-specific identifier that may be used to uniquely refer to the sign-in request.
     */
    private String mRequestId;

    /**
     * List of information or references to information the user wishes to have resolved as part of authentication by
     * the relying party. They are expressed as RFC 3986 URIs separated by `\n- `.
     */
    private String[] mResources;

    /**
     * Private default constructor. A new instance can be created with {@link Builder} or {@link Parser}.
     */
    private SiweMessage() {

    }

    /**
     * Verifies the integrity of the fields of this object by checking several fields and the validity of the signature.
     *
     * @param domain            RFC 4501 dns authority that is requesting the signing
     * @param nonce             The nonce issued by the backend
     * @param signature         A valid signature for this message
     * @throws SiweException    An exception is thrown if the signature is invalid or if fields ar missing
     */
    public void verify(String domain, String nonce, String signature) throws SiweException {
        // Verify that the given domain matches the domain of this object
        if (domain == null || domain.isEmpty() || !domain.equals(mDomain)) {
            throw new SiweException("Domain does not match.", ErrorTypes.DOMAIN_MISMATCH);
        }

        // Verify that the given nonce matches the nonce of this object
        if (nonce == null || nonce.isEmpty() || !nonce.equals(mNonce)) {
            throw new SiweException("Nonce does not match.", ErrorTypes.NONCE_MISMATCH);
        }

        long now = OffsetDateTime.now().toEpochSecond();

        // Verify that the message is not yet expired
        if (mExpirationTime != null) {
            long exp = OffsetDateTime.parse(mExpirationTime).toEpochSecond();
            if (now >= exp) {
                throw new SiweException("Message expired on " + mExpirationTime, ErrorTypes.EXPIRED_MESSAGE);
            }
        }

        // Verify that now >= notBefore
        if (mNotBefore != null) {
            long notBefore = OffsetDateTime.parse(mNotBefore).toEpochSecond();
            if (now < notBefore) {
                throw new SiweException("Message is not valid before " + mNotBefore, ErrorTypes.NOT_YET_VALID_MESSAGE);
            }
        }

        // Verify signature
        if (!ValidatorUtils.isValidSignature(this, signature)) {
            throw new SiweException("Invalid signature.", ErrorTypes.INVALID_SIGNATURE);
        }
    }

    /**
     * Validates if the values of this object are present and in the correct format. Does not verify the correctness of
     * these values.
     *
     * @throws {@link SiweException} if a field is invalid
     */
    private void validateMessage() throws SiweException {
        // Check domain
        if (mDomain == null || mDomain.isEmpty()) {
            throw new SiweException("Domain is invalid.", ErrorTypes.INVALID_DOMAIN);
        }

        // Check if address conforms to EIP-55 (address checksum)
        if (mAddress == null || mAddress.isEmpty() || !ValidatorUtils.isEIP55Address(mAddress)) {
            throw new SiweException("Address does not conform to EIP-55.", ErrorTypes.INVALID_ADDRESS);
        }

        // Check statement
        if (mStatement == null) {
            throw new SiweException("Statement is invalid.", ErrorTypes.INVALID_STATEMENT);
        }

        // Check URI
        if (mUri == null || mUri.isEmpty() || !ValidatorUtils.isURI(mUri)) {
            throw new SiweException("URI is not a valid URI.", ErrorTypes.INVALID_URI);
        }

        // Check if version is 1
        if (mVersion == null || !mVersion.equals(new String("1"))) {
            throw new SiweException("Version must be 1.", ErrorTypes.INVALID_MESSAGE_VERSION);
        }

        // Check if nonce is alphanumeric and
        if (mNonce == null || !mNonce.matches("[a-zA-Z0-9]{8,}")) {
            throw new SiweException("Nonce is not alphanumeric or shorter than 8 chars.", ErrorTypes.INVALID_NONCE);
        }

        // Check issuedAt
        if (mIssuedAt == null || !ValidatorUtils.isISO860Format(mIssuedAt)) {
            throw new SiweException("IssuedAt does not conform to ISO-8601.", ErrorTypes.INVALID_TIME_FORMAT);
        }

        // Check if optional field expirationTime is present. If yes, validate format.
        if (mExpirationTime != null && !ValidatorUtils.isISO860Format(mExpirationTime)) {
            throw new SiweException("ExpirationTime does not conform to ISO-8601.", ErrorTypes.INVALID_TIME_FORMAT);
        }

        // Check if optional field notBefore is present. If yes, validate format.
        if (mNotBefore != null && !ValidatorUtils.isISO860Format(mNotBefore)) {
            throw new SiweException("NotBefore does not conform to ISO-8601.", ErrorTypes.INVALID_TIME_FORMAT);
        }

        // Check if optional field resources is present and not empty. If yes, validate URI format
        if (mResources != null && mResources.length > 0) {
            for (String uri : mResources) {
                if (!ValidatorUtils.isURI(uri)) {
                    throw new SiweException("Resources contains an invalid URI.", ErrorTypes.INVALID_RESOURCES);
                }
            }
        }
    }

    /**
     * This method parses all the fields in the object and returns a valid EIP-4361 string.
     *
     * @return Returns a valid EIP-4361 string
     */
    public String toMessage() {
        String message;

        // The switch becomes relevant once there are more than one version
        switch (mVersion) {
            case "1": {
                message = toMessageV1();
                break;
            }
            default: {
                message = toMessageV1();
                break;
            }
        }
        return message;
    }

    /**
     * This method parses all the fields in the object and returns a valid EIP-4361 string.
     *
     * @return Returns a valid EIP-4361 string
     */
    private String toMessageV1() {
        String header = mDomain + " wants you to sign in with your Ethereum account:";
        String uriField = "URI: " + mUri;
        String prefix = header + "\n" + mAddress;
        String versionField = "Version: " + mVersion;

        if (mNonce == null) {
            mNonce = Utils.generateNonce();
        }

        String chainField = "Chain ID: " + mChainId;

        String nonceField = "Nonce: " + mNonce;

        Set<String> suffixArray = new LinkedHashSet<>();
        suffixArray.add(uriField);
        suffixArray.add(versionField);
        suffixArray.add(chainField);
        suffixArray.add(nonceField);

        suffixArray.add("Issued At: " + (mIssuedAt != null ? mIssuedAt : OffsetDateTime.now().toString()));

        if (mExpirationTime != null) {
            suffixArray.add("Expiration Time: " + mExpirationTime);
        }

        if (mNotBefore != null) {
            suffixArray.add("Not Before: " + mNotBefore);
        }

        if (mRequestId != null) {
            suffixArray.add("Request ID: " + mRequestId);
        }

        if (mResources != null) {
            suffixArray.add("Resources:");
            for (String res : mResources) {
                suffixArray.add("- " + res);
            }
        }

        String suffix = String.join("\n", suffixArray);
        prefix = prefix + "\n\n" + mStatement;
        if (mStatement != null) {
            prefix += "\n";
        }
        return prefix + "\n" + suffix;
    }

    /**
     * This builder creates new instances of {@link SiweMessage}.
     */
    public static class Builder {

        private final SiweMessage mSiweMessage;

        /**
         * Constructs a new builder.
         *
         * @param domain   RFC 4501 dns authority that is requesting the signing
         * @param address  Ethereum address performing the signing
         * @param uri      RFC 3986 URI referring to the resource that is the subject of the signing
         * @param version  Current version of the message
         * @param chainId  EIP-155 Chain ID to which the session is bound
         * @param nonce    Randomized token used to prevent replay attacks
         * @param issuedAt ISO 8601 datetime string of the current time
         */
        public Builder(String domain, String address, String uri, String version, int chainId, String nonce,
                       String issuedAt) {
            mSiweMessage = new SiweMessage();
            mSiweMessage.mDomain = domain;
            mSiweMessage.mAddress = address;
            mSiweMessage.mUri = uri;
            mSiweMessage.mVersion = version;
            mSiweMessage.mChainId = chainId;
            mSiweMessage.mNonce = nonce;
            mSiweMessage.mIssuedAt = issuedAt;
        }

        /**
         * Sets a human-readable ASCII assertion that the user will sign. Must not contain '\n'.
         *
         * @param statement The statement
         * @return a reference to this object
         */
        public Builder statement(String statement) {
            mSiweMessage.mStatement = statement;
            return this;
        }

        /**
         * Sets a ISO 8601 datetime string that indicates when the signed authentication message is no longer valid.
         *
         * @param expirationTime The ISO 8601 datetime string
         * @return a reference to this object
         */
        public Builder expirationTime(String expirationTime) {
            mSiweMessage.mExpirationTime = expirationTime;
            return this;
        }

        /**
         * Sets a ISO 8601 datetime string that indicates when the signed authentication message will become valid.
         *
         * @param notBefore The ISO 8601 datetime string
         * @return a reference to this object
         */
        public Builder notBefore(String notBefore) {
            mSiweMessage.mNotBefore = notBefore;
            return this;
        }

        /**
         * Sets a requestId that may be used to uniquely refer to the sign-in request.
         *
         * @param requestId The requestId
         * @return a reference to this object
         */
        public Builder requestId(String requestId) {
            mSiweMessage.mRequestId = requestId;
            return this;
        }

        /**
         * Sets an array of resources
         *
         * @param resources The resources
         * @return a reference to this object
         */
        public Builder resources(String[] resources) {
            mSiweMessage.mResources = resources;
            return this;
        }

        /**
         * Creates a new {@link SiweMessage} instance with the supplied configuration.
         *
         * @return a new {@link SiweMessage} instance
         * @throws {@link SiweException} if a field is invalid
         */
        public SiweMessage build() throws SiweException {
            // After all fields are set, check if all mandatory fields are present and in the correct format.
            mSiweMessage.validateMessage();
            return mSiweMessage;
        }

    }

    /**
     * An ABNF (Augmented Backus-Naur Form) parser for EIP-4361 strings
     */
    public static class Parser {
        @Accessors(prefix = "m")
        @Getter
        private String mDomain;
        private String mAddress;
        private String mStatement;
        private String mUri;
        private String mVersion;
        private int mChainId;
        private String mNonce;
        private String mIssuedAt;
        private String mExpirationTime;
        private String mNotBefore;
        private String mRequestId;
        private String[] mResources;

        public Parser() {

        }

        /**
         * Tries to parse the given string. The given string must be an EIP-4361 formatted message, otherwise an
         * exception is thrown.
         *
         * @param msg A valid EIP-4361 message
         * @throws {@link SiweException} if the parsing fails
         */
        public SiweMessage parse(String msg) throws SiweException {
            org.mn.web3login.siwe.grammar.apg.Parser parser =
                    new org.mn.web3login.siwe.grammar.apg.Parser(SiweGrammar.getInstance());
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
                org.mn.web3login.siwe.grammar.apg.Parser.Result parse = parser.parse();

                if (!parse.success()) {
                    throw new SiweException("ABNF parsing failed", ErrorTypes.UNABLE_TO_PARSE);
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
                throw new SiweException("Id out of range. Parsing failed", ErrorTypes.UNABLE_TO_PARSE);
            }

            SiweMessage siweMessage = new SiweMessage();
            siweMessage.mDomain = mDomain;
            siweMessage.mAddress = mAddress;
            siweMessage.mStatement = mStatement;
            siweMessage.mUri = mUri;
            siweMessage.mVersion = mVersion;
            siweMessage.mChainId = mChainId;
            siweMessage.mNonce = mNonce;
            siweMessage.mIssuedAt = mIssuedAt;
            siweMessage.mExpirationTime = mExpirationTime;
            siweMessage.mNotBefore = mNotBefore;
            siweMessage.mRequestId = mRequestId;
            siweMessage.mResources = mResources;

            // After all fields are set, check if all mandatory fields are present and in the correct format.
            siweMessage.validateMessage();

            return siweMessage;
        }

        private class AstTranslator extends Ast.AstCallback {
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
                        mResources =
                                Arrays.stream(tmp).map(s -> s.replace("- ", "")).collect(Collectors.toList()).toArray(new String[0]);
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

}
