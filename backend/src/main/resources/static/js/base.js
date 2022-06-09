import { ethers } from '/js/ethers.js';

const ethereum = window.ethereum;

let signer;

function connectWallet() {
    if (!ethereum) {
        showError('You need to install MetaMask.');
        return;
    }

    const provider = new ethers.providers.Web3Provider(ethereum);

    signer = provider.getSigner();

    provider.send('eth_requestAccounts', [])
        .then(() => {
            hideError();
            hideConnectWalletButton();
            showContent();
        })
        .catch(() => {
            showError('Connecting wallet was rejected.');
            showConnectWalletButton();
        });
}

async function getAccountAddress() {
    return await signer.getAddress();
}

async function signMessage(message) {
    return await signer.signMessage(message);
}

function showError(message) {
    const div = document.getElementById('error-div');
    div.innerHTML = message;
    div.style.display = 'block';
}

function hideError() {
    const div = document.getElementById('error-div');
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

function showContent() {
    const div = document.getElementById('content-div');
    div.style.display = 'block';
}

export { connectWallet, getAccountAddress, signMessage };