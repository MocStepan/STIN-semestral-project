import {Injectable} from "@angular/core";
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  successNotification(successMessage: string): void {
    Swal.fire({
      position: 'top-right',
      icon: 'success',
      title: successMessage,
      showConfirmButton: false,
      timer: 5000,
      toast: true,
    })
  }

  errorNotification(errorMessage: string): void {
    Swal.fire({
      position: 'top-right',
      icon: 'error',
      title: errorMessage,
      showConfirmButton: false,
      timer: 5000,
      toast: true,
    })
  }
}
