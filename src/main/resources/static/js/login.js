import { initWeb3Modal, connectWallet, getChainId, getAccountAddress, signMessage } from '/js/base.js';

const connectWalletHandler = function() {
    connectWallet(connectWalletSuccessCallback, connectWalletErrorCallback);
}

const connectWalletSuccessCallback = function () {
    hideConnectWalletButton();
    showInfo('Please check your wallet to sign the message.');
    loginInWithEthereum();
}

const connectWalletErrorCallback = function () {
    hideInfo();
    showConnectWalletButton();
}

const connectWalletButton = document.getElementById('connect-wallet-button');
connectWalletButton && connectWalletButton.addEventListener('click', connectWalletHandler, false);

function showConnectWalletButton() {
    connectWalletButton.style.display = 'inline-block';
}

function hideConnectWalletButton() {
    connectWalletButton.style.display = 'none';
}

const infoContainer = document.getElementById('info-container');

function showInfo(message) {
    infoContainer.innerHTML = message;
    infoContainer.style.display = 'block';
}

function hideInfo() {
    infoContainer.style.display = 'none';
}

// Submits the form
async function loginInWithEthereum() {
    // Create message
    const chainId = await getChainId();
    const address = await getAccountAddress();
    const message = await createLoginMessage(chainId, address);

    // Sign message
    signMessage(message)
        .then((signature) => {
            // Update form
            document.getElementById('siwe-message').value = window.btoa(message);
            document.getElementById('siwe-signature').value = window.btoa(signature);

            // Submit form
            const form = document.getElementById('login-form');
            form.submit();
        })
        .catch(() => {
            showInfo('Please sign the message to sign in.');
            showConnectWalletButton();
        });
}

// Creates a valid EIP-4361 string
async function createLoginMessage(chainId, address) {
    const query = 'chain_id=' + chainId + '&address=' + address;
    const res = await fetch('/login-message?' + query, {
        credentials: 'include',
    });
    return await res.text();
}

initWeb3Modal();