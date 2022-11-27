const Web3Modal = window.Web3Modal.default;
const WalletConnectProvider = window.WalletConnectProvider.default;

let web3Modal;
let web3ModalProvider;
let provider;
let signer;

// Initializes Web3Modal
function initWeb3Modal() {
    const providerOptions = {
        walletconnect: {
            package: WalletConnectProvider,
            options: {
                infuraId: 'b5f225423daa44d092673365889de8b7',
            }
        }
    };

    web3Modal = new Web3Modal({
        cacheProvider: false,
        disableInjectedProvider: false,
        providerOptions
    });
}

// Pops up the Web3Modal and connects to the wallet the user has selected
async function connectWallet(successCallback, errorCallback) {
    try {
        // Open modal
        web3ModalProvider = await web3Modal.connect();
    } catch(e) {
        console.log('Could not get a wallet connection.');
        errorCallback()
        return;
    }

    provider = new ethers.providers.Web3Provider(web3ModalProvider);
    signer = provider.getSigner();

    provider.listAccounts()
        .then(() => {
            successCallback();
        })
        .catch(() => {
            errorCallback();
        })
}

// Returns the chain ID of the used network
async function getChainId() {
    const { chainId } = await provider.getNetwork();
    return chainId;
}

// Returns the account address
async function getAccountAddress() {
    return await signer.getAddress();
}

// Signs a provides message
async function signMessage(message) {
    return await signer.signMessage(message);
}

export { initWeb3Modal, connectWallet, getChainId, getAccountAddress, signMessage };