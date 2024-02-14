const formError = document.getElementById("formError");
const form = document.getElementById("register-form");
const validateForm = () => {
  formError.innerText = "";
  const name = document.getElementById("name").value;
  const email = document.getElementById("email").value;
  const aadhaar = document.getElementById("aadhaar").value;
  const phoneNumber = document.getElementById("phoneNumber").value;
  const password = document.getElementById("password").value;
  const retryPassword = document.getElementById("re-pass").value;
  const regexValidation = {
    phone: /^\d{10}$/,
    email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    aadhaar: /^\d{12}$/,
    password:
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/,
  };
  console.log(name);
  if (name && name.length < 4) {
    console.error("error name");
    formError.innerHTML =
      "Invalid username! username should have atleast 4 characters";
    return false;
  }
  if (aadhaar !== "" && !regexValidation.aadhaar.test(aadhaar)) {
    console.log(aadhaar);
    console.error("error aaadhar");
    formError.innerText = "Invalid Aadhaar number";
    return false;
  }
  if (phoneNumber !== "" && !regexValidation.phone.test(phoneNumber)) {
    console.error("error phoneNumber");
    formError.innerText = "Invalid phone number";
    return false;
  }
  if (password !== "" && !regexValidation.password.test(password)) {
    let passError = "Invalid password! Password must:";

    if (!/[a-z]/.test(password)) {
      passError = passError + "\n- Contain at least one lowercase letter";
    }

    if (!/[A-Z]/.test(password)) {
      passError = passError + "\n- Contain at least one uppercase letter";
    }

    if (!/\d/.test(password)) {
      passError = passError + "\n- Contain at least one number";
    }

    if (!/[@$!%*?&]/.test(password)) {
      passError = passError + "\n- Contain at least one special character";
    }

    if (password.length < 8 || password.length > 20) {
      passError = passError + "\n- Be between 8 and 20 characters long";
    }
    formError.innerText = passError;
    return false;
  }
  if (retryPassword !== password) {
    formError.innerText =
      "The password you re entered does not match with the original password";
    return false;
  }
  return true;
};
form.addEventListener("submit", (e) => {
  e.preventDefault();
  if (validateForm()) {
    form.submit();
  }
});
