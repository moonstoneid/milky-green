import { ethers } from 'ethers';
import { SiweMessage } from 'siwe';
declare var window: any


const domain = window.location.host;
const origin = window.location.origin;
const ethereum = window.ethereum;

var provider;
var signer:ethers.providers.JsonRpcSigner;

/**
 * Creates a EIP-4361 compatible message.
 * 
 * @param address The user's ethereum account address
 * @param statement Call-to-action string that is displayed in MetaMask
 * @returns EIP-4361 compatible message
 * 
 */
async function createSiweMessage(address:string, statement:string) {
    const res = await fetch('/nonce', {
        credentials: 'include',
    });
    const message = new SiweMessage({
        domain,
        address,
        statement,
        uri: origin,
        version: '1',
        chainId: 1,
        nonce: await res.text()
    });
    return message.prepareMessage();
}

/**
 * Creates a EIP-4361 compatible message and asks the user to sign the message with his private key.
 * The signed message is then sent to the backend for verification.
 * 
 */
async function signInWithEthereum() {
    // Create and sign message
    let message = await createSiweMessage(
        await signer.getAddress(),
        'Sign in with Ethereum to the app.'
    );

    const signature = await signer.signMessage(message);
    const form = document.createElement('form');
    form.method = 'post';
    form.action = '/login';

    const messageField = document.createElement('input');
    messageField.type = 'hidden';
    messageField.name = 'message';
    messageField.value = window.btoa(message); // encode Base64
    form.appendChild(messageField);

    const signatureField = document.createElement('input');
    signatureField.type = 'hidden';
    signatureField.name = 'signature';
    signatureField.value = window.btoa(signature); // encode Base64
    form.appendChild(signatureField);

    document.body.appendChild(form);

    // Set CSRF (CSRF is enabled by default in spring boot), otherwise request is rejected
    const csrfField = document.createElement('input');
    csrfField.type = 'hidden';
    csrfField.name = '_csrf';
    csrfField.value = document.querySelector("meta[name='_csrf']").getAttribute("content");
    form.appendChild(csrfField);

    form.submit();
}

async function authorizeWithEthereum(evt: Event) {
    // prevent default form submission of the browser
    evt.preventDefault();

    const oauthClientId = document.getElementById('oauthClientId').innerHTML;

    // Create and sign message
    let message = await createSiweMessage(
        await signer.getAddress(),
        'Authorize the following OAuth ClientID: ' + oauthClientId
    );
    const signature = await signer.signMessage(message);

    (<HTMLInputElement>document.getElementById('siweMessage')).value = window.btoa(message);
    (<HTMLInputElement>document.getElementById('siweSignature')).value = window.btoa(signature);

    const form = (<HTMLFormElement>document.getElementById('consentForm'));
    form.submit();
}

/**
 * Checks if MetaMask is installed. 
 * If true, it requests access to the MetaMask account.
 * 
 */
async function connectWallet() {
    if (!ethereum) {
        let div = document.getElementById('errorDiv');
        div.innerHTML = 'You need to install MetaMask.'
        div.style.visibility ='visible';

        let loginButton = document.getElementById('loginButton');
        loginButton.style.visibility = 'hidden';

        return;    
    }

    // Check chainId
    provider = new ethers.providers.Web3Provider(ethereum);
    let chainId = await getChainId(provider);
    if(chainId != 1){
        try {
            // check if the chain to connect to is installed
            await window.ethereum.request({
                method: 'wallet_switchEthereumChain',
                params: [{ chainId: '0x1' }], // chainId must be in hexadecimal numbers
            });
        } catch (error) {
            return;
        }
    }
    
    // Connect wallet
    signer = provider.getSigner();
    provider.send('eth_requestAccounts', []).catch(() => console.log('user rejected request'));
}

/**
* Returns the chainId currently set in MetaMask
*
*/
async function getChainId(provider: ethers.providers.Web3Provider) {
    const network = await provider.getNetwork();
    const chainId = network.chainId;
    return chainId;
}


// Register events
var loginButton = document.getElementById('loginButton');
loginButton && loginButton.addEventListener ("click", signInWithEthereum, false); // Add eventListener if element exists

var consentApproveButton = document.getElementById('consentApproveButton');
consentApproveButton && consentApproveButton.addEventListener('click', function(e) {authorizeWithEthereum(e);}, false);

var consentDenyButton = document.getElementById('consentDenyButton');
consentDenyButton && consentDenyButton.addEventListener('click', function(e) {authorizeWithEthereum(e);}, false);

// Try to connect wallet on page load
connectWallet();