package com.moonstoneid.web3login.ens.resolver;

import com.moonstoneid.web3login.ens.model.TextRecords;
import com.moonstoneid.web3login.ens.model.TextRecordsInterface;
import org.web3j.crypto.WalletUtils;
import org.web3j.ens.EnsResolutionException;
import org.web3j.ens.EnsResolver;
import org.web3j.ens.NameHash;
import org.web3j.ens.contracts.generated.OffchainResolverContract;
import org.web3j.protocol.Web3j;

/**
 * Extends the {@link EnsResolver} to be able to call further methods of the resolver contract, in particular the
 * text(...) method, which retrieves text records stored for a given .eth domain.
 */
public class EnsRecordsResolver extends EnsResolver {

    private final int addrLength;

    public EnsRecordsResolver(Web3j web3j, long syncThreshold, int addressLength) {
        super(web3j, syncThreshold, addressLength);
        addrLength = addressLength;
    }

    public EnsRecordsResolver(Web3j web3j, long syncThreshold) {
        super(web3j, syncThreshold);
        addrLength = 40;
    }

    public EnsRecordsResolver(Web3j web3j) {
        super(web3j);
        addrLength = 40;
    }

    /**
     * Tries to return the value for a given text record key.
     *
     * Internally, it uses {@link org.web3j.ens.contracts.generated.PublicResolver#text(byte[], String)}} method.
     * This method calls <a href="https://github.com/ensdomains/ens-contracts/blob/8a2423829a28852297ee208357d148987e8dce0f/contracts/resolvers/profiles/ITextResolver.sol#L17">this contract method</a>
     * to get the text record associated with an ens name.
     * @param address           An ethereum address
     * @param textRecordKey     A {@link TextRecords} key
     *
     * @return The value for the given key if it exists. Otherwise it returns an empty string.
     */
    public String getTextRecordUsingAddress(String address, String textRecordKey) {
        if (WalletUtils.isValidAddress(address, addrLength)) {
            String ensName = reverseResolve(address);
            return getTextRecord(ensName, TextRecords.valueOf(textRecordKey));
        } else {
            throw new EnsResolutionException("Address is invalid: " + address);
        }
    }

    /**
     * Tries to return the value for a given text record key.
     *
     * Internally, it uses {@link org.web3j.ens.contracts.generated.PublicResolver#text(byte[], String)}} method.
     * This method calls <a href="https://github.com/ensdomains/ens-contracts/blob/8a2423829a28852297ee208357d148987e8dce0f/contracts/resolvers/profiles/ITextResolver.sol#L17">this contract method</a>
     * to get the text record associated with an ens name.
     * @param ensName           A .eth address
     * @param textRecordKey     A {@link TextRecords} key
     *
     * @return The value for the given key if it exists. Otherwise, it returns an empty string.
     */
    public <T extends Enum<?> & TextRecordsInterface> String getTextRecord(String ensName, T textRecordKey) {
        OffchainResolverContract resolver = obtainOffchainResolver(ensName);
        byte[] nameHash = NameHash.nameHashAsBytes(ensName);

        String value;
        try {
            value = resolver.text(nameHash, textRecordKey.getValue()).send();
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute Ethereum request", e);
        }
        return value;
    }

}
