import { createContext, useContext, useState } from "react";
import ConfirmModal from "../components/common/ConfirmModal";


const ConfirmContext = createContext();

export const ConfirmProvider = ({ children }) => {
  const [modalState, setModalState] = useState({
    isOpen: false,
    title: "",
    message: "",
    danger: false,
    resolve: null
  });

  const confirm = ({ title, message, danger = false }) => {
    return new Promise((resolve) => {
      setModalState({
        isOpen: true,
        title,
        message,
        danger,
        resolve
      });
    });
  };

  const handleConfirm = () => {
    modalState.resolve(true);
    setModalState({ ...modalState, isOpen: false });
  };

  const handleCancel = () => {
    modalState.resolve(false);
    setModalState({ ...modalState, isOpen: false });
  };

  return (
    <ConfirmContext.Provider value={confirm}>
      {children}

      <ConfirmModal
        isOpen={modalState.isOpen}
        title={modalState.title}
        message={modalState.message}
        danger={modalState.danger}
        onConfirm={handleConfirm}
        onCancel={handleCancel}
      />
    </ConfirmContext.Provider>
  );
};

export const useConfirm = () => useContext(ConfirmContext);
