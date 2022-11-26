package com.moonstoneid.web3login.eth;

import com.moonstoneid.web3login.eth.model.TextRecordsInterface;
import org.web3j.ens.EnsResolver;
import org.web3j.ens.NameHash;
import org.web3j.ens.contracts.generated.OffchainResolverContract;
import org.web3j.protocol.Web3j;

public class EnsRecordsResolver extends EnsResolver {

    public EnsRecordsResolver(Web3j web3j) {
        super(web3j);
    }

    public <T extends Enum<?> & TextRecordsInterface> String getTextRecord(String ensName,
            T textRecordKey) {
        OffchainResolverContract resolver = obtainOffchainResolver(ensName);
        byte[] nameHash = NameHash.nameHashAsBytes(ensName);

        try {
            return resolver.text(nameHash, textRecordKey.getValue()).send();
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute Ethereum request!", e);
        }
    }

}
