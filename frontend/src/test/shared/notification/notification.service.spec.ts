import {NotificationService} from "../../../app/shared/notification/service/notification.service";
import Swal from "sweetalert2";

jest.mock('sweetalert2', () => ({
  fire: jest.fn()
}));

describe('NotificationService', () => {
  let notificationService: NotificationService;

  beforeEach(() => {
    notificationService = new NotificationService();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should display success notification', () => {
    const successMessage = 'Success message';
    notificationService.successNotification(successMessage);
    expect(Swal.fire).toHaveBeenCalledWith({
      position: 'top-right',
      icon: 'success',
      title: successMessage,
      showConfirmButton: false,
      timer: 5000,
      toast: true
    });
  });

  it('should display error notification', () => {
    const errorMessage = 'Error message';
    notificationService.errorNotification(errorMessage);
    expect(Swal.fire).toHaveBeenCalledWith({
      position: 'top-right',
      icon: 'error',
      title: errorMessage,
      showConfirmButton: false,
      timer: 5000,
      toast: true
    });
  });
});
