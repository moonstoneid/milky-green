import { initWeb3Modal, connectWallet, getChainId, getAccountAddress, signMessage } from '/js/base.js';

const connectWalletHandler = function() {
    connectWallet(connectWalletSuccessCallback, connectWalletErrorCallback);
}

const connectWalletSuccessCallback = function () {
    hideConnectWalletButton();
    showConsent();
}

const connectWalletErrorCallback = function () {
    hideConsent();
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

const consentDiv = document.getElementById('consent-div');

const consentApproveButton = document.getElementById('consent-approve-button');
consentApproveButton && consentApproveButton.addEventListener('click',
    function(e) {authorizeWithEthereum(e);}, false);

const consentDenyButton = document.getElementById('consent-deny-button');
consentDenyButton && consentDenyButton.addEventListener('click',
    function(e) {authorizeWithEthereum(e);}, false);

function showConsent() {
    consentDiv.style.display = 'block';
}

function hideConsent() {
    consentDiv.style.display = 'none';
}

// Submits the form
async function authorizeWithEthereum(evt) {
    evt.preventDefault();

    // Get client ID for consent message
    const oauthClientId = document.getElementById('oauth-client-id').innerHTML;

    // Create and sign message
    const chainId = await getChainId();
    const address = await getAccountAddress();
    const message = await createConsentMessage(chainId, address, oauthClientId);
    const signature = await signMessage(message);

    // Update form
    document.getElementById('siwe-message').value = window.btoa(message);
    document.getElementById('siwe-signature').value = window.btoa(signature);

    // Submit form
    const form = document.getElementById('consent-form');
    form.submit();
}

// Creates a valid EIP-4361 string
async function createConsentMessage(chainId, address, clientId) {
    const query = 'chain_id=' + chainId + '&address=' + address + '&client_id=' + clientId ;
    const res = await fetch('/consent-message?' + query, {
        credentials: 'include',
    });
    return await res.text();
}

initWeb3Modal();
connectWalletHandler();