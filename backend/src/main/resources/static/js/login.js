import { initWeb3Modal, connectWallet, getChainId, getAccountAddress, signMessage } from '/js/base.js';

const connectWalletHandler = function() {
    connectWallet(connectWalletSuccessCallback, connectWalletErrorCallback);
}

const connectWalletSuccessCallback = function () {
    hideConnectWalletButton();
    showInfo("Please check your wallet to sign the message.");
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

const infoDiv = document.getElementById('info-div');

function showInfo(message) {
    infoDiv.innerHTML = message;
    infoDiv.style.display = 'block';
}

function hideInfo() {
    infoDiv.style.display = 'none';
}

// Submits the form
async function loginInWithEthereum() {
    // Create and sign message
    const chainId = await getChainId();
    const address = await getAccountAddress();
    const message = await createLoginMessage(chainId, address);
    const signature = await signMessage(message);

    // Update form
    document.getElementById('siwe-message').value = window.btoa(message);
    document.getElementById('siwe-signature').value = window.btoa(signature);

    // Submit form
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

initWeb3Modal();
connectWalletHandler();