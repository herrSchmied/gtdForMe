package jborg.gtdForBash;

public enum DimensionsOfValues
{
	
	ONEANDONLY()
	{
		@Override
		public int getLevel(){return 128;}
	},
	
	TOPTIER()
	{
		@Override
		public int getLevel(){return 64;}
	},

	ELEVATED()
	{
		@Override
		public int getLevel(){return 32;}
	},

	MEDIOCRE()
	{
		@Override
		public int getLevel(){return 16;}
	},

	SUBSATISFYING()
	{
		@Override
		public int getLevel(){return 8;}
	},

	LOW()
	{
		@Override
		public int getLevel(){return 4;}
	},

	TERRIBLE()
	{
		@Override
		public int getLevel(){return 2;}
	};
	
	public int getLevel()
	{
		return -1;
	}
}
