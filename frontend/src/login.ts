import {signer, connectWallet, getNonce, createMessage} from './base';

async function signInWithEthereum(evt: Event): Promise<void> {
    // Prevent default form submission of the browser
    evt.preventDefault();

    // Get message contents
    let address = await signer.getAddress();
    let statement = 'Sign in with Ethereum to the app.';
    let nonce = await getNonce();

    // Create and sign message
    const message = await createMessage(address, statement, nonce);
    const signature = await signer.signMessage(message);

    (<HTMLInputElement> document.getElementById('siweMessage')).value = window.btoa(message);
    (<HTMLInputElement> document.getElementById('siweSignature')).value = window.btoa(signature);

    const form = (<HTMLFormElement>document.getElementById('loginForm'));
    form.submit();
}

const connectWalletButton = document.getElementById('connectWalletButton');
connectWalletButton && connectWalletButton.addEventListener('click', connectWallet, false);

const loginButton = document.getElementById('loginButton');
loginButton && loginButton.addEventListener('click',
    function(e: MouseEvent) {signInWithEthereum(e);},
    false);

// Try to connect wallet on page load
connectWallet();