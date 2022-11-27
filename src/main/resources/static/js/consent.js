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

const consentContainer = document.getElementById('consent-container');

function showConsent() {
    consentContainer.style.display = 'block';
}

function hideConsent() {
    consentContainer.style.display = 'none';
}

const consentApproveButton = document.getElementById('consent-approve-button');
consentApproveButton && consentApproveButton.addEventListener('click',
    function(e) {approveAuthorizeWithEthereum(e);}, false);

const consentDenyButton = document.getElementById('consent-deny-button');
consentDenyButton && consentDenyButton.addEventListener('click',
    function(e) {denyAuthorizeWithEthereum(e);}, false);

// Submits the form with "approve"
async function approveAuthorizeWithEthereum(evt) {
    evt.preventDefault();

    // Get client ID for consent message
    const oauthClientId = document.getElementById('oauth-client-id').innerHTML;

    // Create message
    const chainId = await getChainId();
    const address = await getAccountAddress();
    const message = await createConsentMessage(chainId, address, oauthClientId);

    // Sign message
    signMessage(message)
        .then((signature) => {
            // Update form
            document.getElementById('siwe-message').value = window.btoa(message);
            document.getElementById('siwe-signature').value = window.btoa(signature);

            // Submit form
            const form = document.getElementById('consent-form');
            form.submit();
        });
}

// Submits the form with "deny"
async function denyAuthorizeWithEthereum(evt) {
    evt.preventDefault();

    // Submit form
    const form = document.getElementById('consent-form');
    form.reset();
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