const StatCard = ({ title, value, icon }) => {
  return (
    <div className="bg-white rounded-xl shadow-md p-5 border border-slate-200 flex items-center gap-4 hover:shadow-lg transition">
      
      {icon && (
        <div className="text-3xl">
          {icon}
        </div>
      )}

      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <h2 className="text-2xl font-bold text-indigo-700 mt-1">
          {value ?? 0}
        </h2>
      </div>
    </div>
  );
};

export default StatCard;
