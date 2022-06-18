import { ethers } from '/js/ethers.js';

const Web3Modal = window.Web3Modal.default;
const WalletConnectProvider = window.WalletConnectProvider.default;

let signer;
let web3Modal;
let web3ModalProvider;
let provider;

// Initializes Web3Modal
function initWeb3Modal() {
    const providerOptions = {
        walletconnect: {
            package: WalletConnectProvider,
            options: {
                infuraId: "b5f225423daa44d092673365889de8b7",
            }
        }
    };

    web3Modal = new Web3Modal({
        cacheProvider: false, // optional
        providerOptions, // required
        disableInjectedProvider: false
    });
}

// Pops up the Web3Modal and connects to the wallet the user has selected
async function connectWallet() {

    try {
        // Open modal
        web3ModalProvider = await web3Modal.connect();
    } catch(e) {
        console.log("Could not get a wallet connection.");
        showConnectWalletButton();
    return;
    }

    provider = new ethers.providers.Web3Provider(web3ModalProvider);
    signer = provider.getSigner();

    provider.listAccounts()
        .then(async result => {
            hideConnectWalletButton();
            showInfo("Please check your wallet to sign the message.");
            await loginInWithEthereum();
        })
        .catch(error => {
            hideInfo();
            showConnectWalletButton();
        })
}

// Submits the login form
async function loginInWithEthereum() {
      // Create and sign message
      const address = await getAccountAddress();
      const { chainId } = await provider.getNetwork();
      const message = await createLoginMessage(chainId, address);
      const signature = await signMessage(message);

      document.getElementById('siwe-message').value = window.btoa(message);
      document.getElementById('siwe-signature').value = window.btoa(signature);

      const form = document.getElementById('login-form');
      form.submit();
}

// Creates a valid EIP-4361 string
async function createLoginMessage(chainId, address) {
      const query = 'chain_id=' + chainId + '&address=' + address;
      const res = await fetch('/login-message?' + query, {
        credentials: 'include',
      });
      return await res.text();
}

async function getAccountAddress() {
    return await signer.getAddress();
}

async function signMessage(message) {
    return await signer.signMessage(message);
}

function showInfo(message) {
    const div = document.getElementById('info-div');
    div.innerHTML = message;
    div.style.display = 'block';
}

function hideInfo() {
    const div = document.getElementById('info-div');
    div.style.display = 'none';
}

function showConnectWalletButton() {
    const button = document.getElementById('connect-wallet-button');
    button.style.display = 'inline-block';
}

function hideConnectWalletButton() {
    const button = document.getElementById('connect-wallet-button');
    button.style.display = 'none';
}

export { initWeb3Modal, connectWallet };