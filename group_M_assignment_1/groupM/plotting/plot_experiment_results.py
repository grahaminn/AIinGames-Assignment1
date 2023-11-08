import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

COLORS = ['lightblue', 'lightgreen', 'pink', 'lightseagreen', 'moccasin', 'lightsteelblue' ]



def load_experiment_data(experiment_dir, agent_names):
    n_agents = len(agent_names)
    data = pd.read_csv(f"experiments/{experiment_dir}/results/GAME_OVER.csv")
    n_games = len(data)
    n_matchups = 2 * int(n_games / n_agents)
    results = np.zeros((n_matchups, n_agents))
    
    for i, agent in enumerate(agent_names):
        player_0 = data[data["FinalScore(PlayerName-0)"] == agent]
        player_1 = data[data["FinalScore(PlayerName-1)"] == agent]
        player_data_0 = player_0[f"FinalScore(Player-0)"].to_numpy()
        player_data_1 = player_1[f"FinalScore(Player-1)"].to_numpy()
        player_data = np.concatenate([player_data_0, player_data_1], axis=0)
        results[:, i] = player_data
    
    return results

def plot_experiment_results(experiment_dir, agent_names, display_names, title, colors=None, save_filename=None, ax=None, set_y_label=True):
    results =  load_experiment_data(experiment_dir, agent_names)
    
    # sort by mean result
    mean_results = results.mean(0)
    sorted_idx = np.flip(mean_results.argsort())
    ordered_results = results[:, sorted_idx]
    ordered_agents = np.array(display_names)[sorted_idx]
    
    if ax is None:
        ax = plt.subplot(1, 1, 1)
        
    bplot = ax.boxplot(ordered_results, patch_artist=True, labels=ordered_agents)
    
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

def plot_effect_of_hyperparam(experiements_dirs, agent_names, param_values, xlabel, title):
    n_agents = len(agent_names)
    # n agents x (min score, av score, max score, win rate)
    min_score = np.zeros((n_agents, ))
    mean_score = np.zeros((n_agents, ))
    max_score = np.zeros((n_agents, ))
    win_rate = np.zeros((n_agents, ))
    
    for i, (experiment_dir, agent_name) in enumerate(zip(experiements_dirs, agent_names)):
        results = load_experiment_data(experiment_dir, agent_name)
        min_score[i] = results[1].min()
        max_score [i] = results[1].max()
        mean_score[i] = results[1].mean()
        win_rate[i] = (results[:, 1] > results[:, 0]).sum() * 100 / len(results) 
    
    score_ax = plt.gca()
    win_ax = score_ax.twinx()
    
    score_ax.plot(param_values, min_score, label='min score')
    score_ax.plot(param_values, max_score, label='max score')
    score_ax.plot(param_values, mean_score, label='av score')
    win_ax.plot(param_values, win_rate, label='win rate')
    
    score_ax.set_ylabel('Score')
    win_ax.set_ylabel('Win rate')
    plt.xlabel(xlabel)
    plt.title(title)
    win_ax.legend(loc=0)
    score_ax.legend(loc=0)

    plt.show()
    
if __name__ == "__main__":
    
    amaf_v = [5, 10, 20]
    colours = [[COLORS[0], COLORS[i+1]] for i in range(len(amaf_v))]
    experiment_dirs = [f'{i}roots' for i in amaf_v]
    agent_names = [['1 root', f'{i} roots ucb'] for i in amaf_v]
    display_names = [['1 Root', f'{i} Roots'] for i in amaf_v]
    titles = [f'{i} Roots VS 1 Root' for i in amaf_v]

    # # plot_experiment_results('thompsonVsGroupM',['BasicMCTS', 'Thompson'], ['UCB1', 'Thompson'], '')   
    plot_multi_experiment_subplots(experiment_dirs, agent_names, display_names, titles)
    # plot_effect_of_hyperparam(experiment_dirs, agent_names, amaf_v, 'amafV', 'ttt')
    # plt.show()
    
    # data = load_experiment_data('final', ['AMAF', 'BasicMCTS', 'Multi-root MCTS', 'Random', 'rhea', 'Thompson', 'UCT Bayes'])
    # plot_experiment_results('final',['AMAF', 'BasicMCTS', 'Multi-root MCTS', 'Random', 'rhea', 'Thompson', 'UCT Bayes'],['AMAF', 'BasicMCTS', 'Multi-root', 'Random', 'RHEA', 'Thompson', 'Bayes-UCB'], '500 games in Sushi Go!' )   
    plt.show()