import {
  useFloating,
  offset,
  flip,
  shift,
  autoUpdate
} from "@floating-ui/react";
import { useState, useEffect } from "react";
import { createPortal } from "react-dom";

const ActionDropdown = ({
  user,
  isSuper,
  isAdmin,
  isDeleted,
  toggleUser,
  makeAdmin,
  demoteAdmin,
  deleteUser,
  restoreUser,
  confirmAndRun
}) => {
  const [open, setOpen] = useState(false);

  const { refs, floatingStyles } = useFloating({
    placement: "bottom-end",
    middleware: [
      offset(6),
      flip({ fallbackPlacements: ["top-end"] }),
      shift({ padding: 8 })
    ],
    whileElementsMounted: autoUpdate
  });

  // Close on outside click
  useEffect(() => {
    const handleClick = (e) => {
      if (
        refs.reference.current &&
        !refs.reference.current.contains(e.target) &&
        refs.floating.current &&
        !refs.floating.current.contains(e.target)
      ) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClick);
    return () =>
      document.removeEventListener("mousedown", handleClick);
  }, [refs]);

  return (
    <>
      <button
        ref={refs.setReference}
        onClick={() => setOpen(!open)}
        className="px-3 py-1 text-xs border border-gray-300 rounded hover:bg-gray-100"
      >
        Actions ▾
      </button>

      {open &&
        createPortal(
          <div
            ref={refs.setFloating}
            style={floatingStyles}
            className="w-44 bg-white border border-gray-200 rounded shadow-lg z-[9999]"
          >
            {/* Enable / Disable */}
            {!isDeleted && (
              <button
                onClick={() => {
                  setOpen(false);
                  confirmAndRun(
                    {
                      title: user.active
                        ? "Disable User"
                        : "Enable User",
                      message: `Are you sure you want to ${
                        user.active ? "disable" : "enable"
                      } ${user.email}?`,
                      danger: user.active
                    },
                    () => toggleUser(user.uid),
                    user.active
                      ? "User disabled"
                      : "User enabled"
                  );
                }}
                className="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              >
                {user.active ? "Disable" : "Enable"}
              </button>
            )}

            {/* Promote */}
            {isSuper && user.role === "USER" && !isDeleted && (
              <button
                onClick={() => {
                  setOpen(false);
                  confirmAndRun(
                    {
                      title: "Promote to Admin",
                      message: `Make ${user.email} an ADMIN?`
                    },
                    () => makeAdmin(user.uid),
                    "User promoted"
                  );
                }}
                className="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              >
                Promote
              </button>
            )}

            {/* Demote */}
            {isSuper && user.role === "ADMIN" && !isDeleted && (
              <button
                onClick={() => {
                  setOpen(false);
                  confirmAndRun(
                    {
                      title: "Demote Admin",
                      message: `Demote ${user.email} to USER?`,
                      danger: true
                    },
                    () => demoteAdmin(user.uid),
                    "Admin demoted"
                  );
                }}
                className="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              >
                Demote
              </button>
            )}

            {/* Delete */}
            {!isDeleted &&
              (isSuper ||
                (isAdmin && user.role === "USER")) && (
                <button
                  onClick={() => {
                    setOpen(false);
                    confirmAndRun(
                      {
                        title: "Delete User",
                        message: `Delete ${user.email}?`,
                        danger: true
                      },
                      () => deleteUser(user.uid),
                      "User deleted"
                    );
                  }}
                  className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  Delete
                </button>
              )}

            {/* Restore */}
            {isDeleted && isSuper && (
              <button
                onClick={() => {
                  setOpen(false);
                  confirmAndRun(
                    {
                      title: "Restore User",
                      message: `Restore ${user.email}?`
                    },
                    () => restoreUser(user.uid),
                    "User restored"
                  );
                }}
                className="block w-full text-left px-4 py-2 text-sm text-green-600 hover:bg-green-50"
              >
                Restore
              </button>
            )}
          </div>,
          document.body
        )}
    </>
  );
};

export default ActionDropdown;
