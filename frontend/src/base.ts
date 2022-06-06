import { ethers } from 'ethers';

declare var window: any

const ethereum = window.ethereum;

let signer: ethers.providers.JsonRpcSigner;

function connectWallet() {
    if (!ethereum) {
        showError('You need to install MetaMask.');
        return;
    }

    // Get provider
    const provider = new ethers.providers.Web3Provider(ethereum);

    // Connect wallet
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

function showError(message: string) {
    const div = document.getElementById('errorDiv');
    div.innerHTML = message;
    div.style.display = 'block';
}

function hideError() {
    const div = document.getElementById('errorDiv');
    div.style.display = 'none';
}

function showConnectWalletButton() {
    const button = document.getElementById('connectWalletButton');
    button.style.display = 'block';
}

function hideConnectWalletButton() {
    const button = document.getElementById('connectWalletButton');
    button.style.display = 'none';
}

function showContent() {
    const div = document.getElementById('contentDiv');
    div.style.display = 'block';
}

export {signer, connectWallet}