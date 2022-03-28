package org.mn.web3login.siwe;


import lombok.Getter;
import org.mn.web3login.siwe.error.ErrorTypes;
import org.mn.web3login.siwe.error.SiweException;
import org.mn.web3login.siwe.parser.ABNFParsedMessage;
import org.mn.web3login.siwe.util.Utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class SiweMessage {

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
     *
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

    public SiweMessage(String msg) throws SiweException {
        ABNFParsedMessage pMsg = new ABNFParsedMessage(msg);

        mDomain = pMsg.getMDomain();
        mAddress = pMsg.getMAddress();
        mStatement = pMsg.getMStatement();
        mUri = pMsg.getMUri();
        mChainId = pMsg.getMChainId();
        mVersion = pMsg.getMVersion();
        mNonce = pMsg.getMNonce();
        mIssuedAt = pMsg.getMIssuedAt();
        mExpirationTime = pMsg.getMExpirationTime();
        mNotBefore = pMsg.getMNotBefore();
        mRequestId = pMsg.getMRequestId();
        mResources = pMsg.getMResources();
    }

    /**
     * This function can be used to retrieve an EIP-4361 formatted message for
     * signature, although you can call it directly it's advised to use
     * [signMessage()] instead which will resolve to the correct method based
     * on the [type] attribute of this object, in case of other formats being
     * implemented.
     *
     * @return This message as an EIP-4361 formatted string
     */
    public String toMessage() {
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
     *  Validates the integrity of the fields of this objects by matching it's
     *  signature.
     *
     * @param signature A valid signature for this message
     * @throws Exception An exception is thrown if the signature is invalid or if fields ar emissing
     */
    public void validate(String signature) throws SiweException {
        String msg = prepareMessage();

        Set<String> missing = new LinkedHashSet<>();
        if(msg == null){
            missing.add("Message");
        }
        if(signature == null){
            missing.add("Signature");
        }
        if(mAddress == null){
            missing.add("Address");
        }
        if(!missing.isEmpty()){
            throw new SiweException("The following fields are missing" + String.join(",", missing), ErrorTypes.MALFORMED_SESSION);
        }

        List<String> list = Utils.validate(msg, signature);

        // TODO: Implement EIP-1271
        // See https://github.com/spruceid/siwe/blob/main/lib/client.ts#L255

        // Check if list contains value, ignore case-sensitivity
        if (!list.stream().anyMatch(mAddress::equalsIgnoreCase)){
            throw new SiweException("Invalid signature.", ErrorTypes.INVALID_SIGNATURE);
        }

        if(mExpirationTime != null) {
            try {
                long exp = OffsetDateTime.parse(mExpirationTime).toEpochSecond();
                long now = OffsetDateTime.now().toEpochSecond();
                if (now >= exp){
                    throw new SiweException("Message expired on " + mExpirationTime, ErrorTypes.EXPIRED_MESSAGE);
                }
            }
            catch (DateTimeParseException e){
                throw new SiweException("Could not parse expiration time.", ErrorTypes.MALFORMED_SESSION);
            }
        }

        if(mNotBefore != null) {
            try {
                long notBefore = OffsetDateTime.parse(mNotBefore).toEpochSecond();
                long now = OffsetDateTime.now().toEpochSecond();
                if (now < notBefore){
                    throw new SiweException("Message is not valid before " + mNotBefore, ErrorTypes.NOTBEFORE_MESSAGE);
                }
            }
            catch (DateTimeParseException e){
                throw new SiweException("Could not parse notBefore time.", ErrorTypes.MALFORMED_SESSION);
            }
        }
    }

    /**
     * This method parses all the fields in the object and creates a sign
     * message according with the type defined.
     *
     * @return Returns a message ready to be signed according with the type defined in the object.
     */
    private String prepareMessage() {
        String message;

        // I know the switch is unnecessary until there are more than one cases
        // I copied it from https://github.com/spruceid/siwe/blob/main/lib/client.ts#L204
        switch (Integer.parseInt(mVersion)) {
            case 1: {
                message = this.toMessage();
                break;
            }
            default: {
                message = this.toMessage();
                break;
            }
        }
        return message;
    }

}
