import {signer, connectWallet} from './base';

async function createLoginMessage(chainId: Number, address: String): Promise<string> {
    const query = 'chain_id=' + chainId + '&address=' + address;
    const res = await fetch('/login-message?' + query, {
        credentials: 'include',
    });
    return await res.text();
}

async function loginInWithEthereum(evt: Event): Promise<void> {
    // Prevent default form submission of the browser
    evt.preventDefault();

    // Create and sign message
    let address = await signer.getAddress();
    const message = await createLoginMessage(1, address);
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
    function(e: MouseEvent) {loginInWithEthereum(e);},
    false);

// Try to connect wallet on page load
connectWallet();