import {signer, connectWallet} from './base';

async function createConsentMessage(chainId: Number, address: String, clientId: String): Promise<string> {
    const query = 'chain_id=' + chainId + '&address=' + address + '&client_id=' + clientId ;
    const res = await fetch('/consent-message?' + query, {
        credentials: 'include',
    });
    return await res.text();
}

async function authorizeWithEthereum(evt: Event): Promise<void> {
    // Prevent default form submission of the browser
    evt.preventDefault();

    const oauthClientId = document.getElementById('oauthClientId').innerHTML;

    // Create and sign message
    let address = await signer.getAddress();
    const message = await createConsentMessage(1, address, oauthClientId);
    const signature = await signer.signMessage(message);

    (<HTMLInputElement> document.getElementById('siweMessage')).value = window.btoa(message);
    (<HTMLInputElement> document.getElementById('siweSignature')).value = window.btoa(signature);

    const form = (<HTMLFormElement> document.getElementById('consentForm'));
    form.submit();
}

const connectWalletButton = document.getElementById('connectWalletButton');
connectWalletButton && connectWalletButton.addEventListener('click', connectWallet, false);

const consentApproveButton = document.getElementById('consentApproveButton');
consentApproveButton && consentApproveButton.addEventListener('click',
        function(e: MouseEvent) {authorizeWithEthereum(e);},
        false);

const consentDenyButton = document.getElementById('consentDenyButton');
consentDenyButton && consentDenyButton.addEventListener('click',
    function(e: MouseEvent) {authorizeWithEthereum(e);},
    false);

// Try to connect wallet on page load
connectWallet();