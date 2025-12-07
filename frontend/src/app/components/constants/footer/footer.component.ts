import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface ContactFormData {
  email: string;
  subject: string;
  message: string;
}

@Component({
  selector: 'app-footer',
  imports: [CommonModule, FormsModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  contactForm: ContactFormData = {
    email: '',
    subject: '',
    message: ''
  };

  /**
   * Submit contact form
   * In a real application, this would send data to a backend service
   */
  submitContactForm(): void {
    if (this.isFormValid()) {
      console.log('Form submitted:', this.contactForm);

      // Here you would typically call an HTTP service to send the email
      // Example: this.emailService.sendContactForm(this.contactForm).subscribe(...)

      // Reset form after submission
      this.resetForm();
      alert('Merci ! Votre message a été envoyé avec succès.');
    } else {
      alert('Veuillez remplir tous les champs du formulaire.');
    }
  }

  /**
   * Validate form fields
   */
  private isFormValid(): boolean {
    return (
      this.contactForm.email.trim() !== '' &&
      this.contactForm.subject.trim() !== '' &&
      this.contactForm.message.trim() !== '' &&
      this.isValidEmail(this.contactForm.email)
    );
  }

  /**
   * Simple email validation
   */
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Reset form fields
   */
  private resetForm(): void {
    this.contactForm = {
      email: '',
      subject: '',
      message: ''
    };
  }
}