import {signer, connectWallet, getNonce, createMessage} from './base';

async function authorizeWithEthereum(evt: Event): Promise<void> {
    // Prevent default form submission of the browser
    evt.preventDefault();

    const oauthClientId = document.getElementById('oauthClientId').innerHTML;

    // Get message contents
    let address = await signer.getAddress();
    let statement = 'Authorize the following OAuth ClientID: ' + oauthClientId
    let nonce = await getNonce();

    // Create and sign message
    const message = await createMessage(address, statement, nonce);
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