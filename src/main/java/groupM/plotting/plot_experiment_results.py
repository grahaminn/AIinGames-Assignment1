import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

COLORS = ['lightblue', 'lightgreen', 'pink', 'lightseagreen', 'moccasin']


def load_experiment_data(experiment_dir, agent_names):
    n_agents = len(agent_names)
    data = pd.read_csv(f"experiments/{experiment_dir}/results/GAME_OVER.csv")
    n_games = len(data)
    results = np.zeros((n_games, n_agents))
    
    for i, agent in enumerate(agent_names):
        player_0 = data[data["FinalScore(PlayerName-0)"] == agent]
        player_1 = data[data["FinalScore(PlayerName-1)"] == agent]
        player_data_0 = player_0[f"FinalScore(Player-0)"].to_numpy()
        player_data_1 = player_1[f"FinalScore(Player-1)"].to_numpy()
        player_data = np.concatenate([player_data_0, player_data_1], axis=0)
        results[:, i] = player_data
    
    return results

def plot_experiment_results(experiment_dir, agent_names, display_names, title, colors=None, save_filename=None, ax=None, set_y_label=True):
    results = load_experiment_data(experiment_dir, agent_names)
    
    if ax is None:
        ax = plt.subplot(1, 1, 1)
        
    bplot = ax.boxplot(results, patch_artist=True, labels=display_names)
    
    ax.title.set_text(title)
    if set_y_label:
        ax.set_ylabel("Final Score")
    ax.set_xlabel("Agent")
    
    if colors is None:
        colors =  COLORS[:len(agent_names)]
        
    for patch, color in zip(bplot['boxes'], colors):
        patch.set_facecolor(color)    
        
    if save_filename is not None:
        savefig(save_filename, bbox_inches='tight', dpi=500)

    

def plot_multi_experiment_subplots(experiments_dirs, agent_names, display_names, titles):
    n_subplots = len(experiments_dirs)
    for i, (experiment_dir, agent_name, display_name, title) in enumerate(zip(experiments_dirs, agent_names, display_names, titles)):
        ax = plt.subplot(1, n_subplots, i+1)
        colors = [COLORS[0], COLORS[i+1]]
        plot_experiment_results(experiment_dir, agent_name, display_name, title,colors=colors, ax=ax, set_y_label=i==0)


if __name__ == "__main__":
    
    amaf_v = [5, 10, 20, 50]
    colours = [[COLORS[0], COLORS[i+1]] for i in range(len(amaf_v))]
    experiment_dirs = [f'amafFullTest-{i}' for i in amaf_v]
    agent_names = [['BasicMCTS', f'Amaf-v={i}'] for i in amaf_v]
    display_names = [['Normal', 'Amaf'] for i in amaf_v]
    titles = ['' for i in amaf_v]

    # plot_experiment_results('thompsonVsGroupM',['BasicMCTS', 'Thompson'], ['UCB1', 'Thompson'], '')   
    plot_multi_experiment_subplots(experiment_dirs, agent_names, display_names, titles)
    plt.show()