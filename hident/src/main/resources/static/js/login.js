function togglePass() {
    const input = document.getElementById("password");
    const open  = document.getElementById("eye-open");
    const closed = document.getElementById("eye-closed");
    if (input.type === "password") {
      input.type = "text";
      open.classList.add("d-none");
      closed.classList.remove("d-none");
    } else {
      input.type = "password";
      open.classList.remove("d-none");
      closed.classList.add("d-none");
    }
  }
