import { ethers } from 'ethers';
import { SiweMessage } from 'siwe';

declare var window: any

const ethereum = window.ethereum;
const domain = window.location.host;
const origin = window.location.origin;

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

async function getNonce(): Promise<string> {
    const res = await fetch('/nonce', {
        credentials: 'include',
    });
    return await res.text();
}

async function createMessage(address: string, statement: string, nonce: string): Promise<string> {
    const message = new SiweMessage({
        domain: domain,
        address: address,
        statement: statement,
        uri: origin,
        version: '1',
        chainId: 1,
        nonce: nonce
    });
    return message.prepareMessage();
}

export {signer, connectWallet, getNonce, createMessage}